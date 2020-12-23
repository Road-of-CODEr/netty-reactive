# ByteBuf

네티의 데이터 컨테이너 

## index

- [Overview](#Overview)
- [Architecture](Architecture)
  - [힙 버퍼](#힙-버퍼)
  - [다이렉트 버퍼](#다이렉트-버퍼)
  - [복합 버퍼](#복합-버퍼)
  - [읽기와 쓰기](#읽기와-쓰기)
  - [파생 버퍼](#파생-버퍼)
  - [ByteBufHolder](#ByteBufHolder)
  - [할당](#할당)
  - [ByteBufUtil](#ByteBufUtil)
  - [참조 카운팅](#참조-카운팅)



## Overview

`java.NIO.ByteBuffer` 라는 자바 바이트 컨테이너를 제공하지만, 사용법이 복잡하여 이를 대신하는 네티는 `ByteBuf` 클래스를 제공한다.



## Architecture

우선 `ByteBuf` 의 장점을 간단히 알아보고 가자.

- 사용자 정의 버퍼 형식으로 확장 가능
- 내장 복합 버퍼 형식을 통해 투명한 제로 카피 달성 가능
- 용량을 필요에 따라 확장 가능 - `StringBuilder` 처럼
- ByteBuffer()의 filp() 메서드 호출 없이 리더와 라이터 모드의 전환 가능
- 읽기와 쓰기에 고유 인덱스를 적용
- 메서드 체인의 지원
- 참조 카운팅 지원
- 풀링 지원



ByteBuf는 읽기와 쓰기에 관한 두 인덱스를 가지고 있다. (cf. ByteBuffer는 인덱스가 하나만 존재)

- `readerIndex`
  - 데이터를 읽으면 바이트 수만큼 증가
- `writerIndex`
  - 데이터를 기록하면 바이트 수만큼 증가

> ByteBuf의 메서드 중 read 나 write로 시작하는 메서드들은 이 인덱스들을 조절하게 된다. (cf. set, get 은 인덱스를 증가시키지 않는다)



### 힙 버퍼

보조 배열(backing array) 이라고 부르는 가장 **자주 사용**하게 되는 `ByteBuf` 패턴이다. 

이름 그대로 JVM Heap 영역에 저장한다. 또한 풀링이 사용되지 않는 경우 빠른 할당과 해제속도를 보인다.

```java
ByteBuf heapBuf = ...;
if (heapBuf.hasArray()) {  // 보조 배열이 있는지 확인
    byte[] array = heapBuf.array();  // 있는 경우 참조를 얻음
    int offset = heapBuf.arrayOffset() + heapBuf.readerIndex(); // 첫번째 바이트에 대한 오프셋 계산
    int length = heapBuf.readableBytes();  // 읽을 수 있는 바이트 수를 얻음
    handleArray(array, offset, length); // 메서드 호출
}
```



### 다이렉트 버퍼 

주요 특징

- JVM Heap 영역 바깥에 위치한다.
  - 네트워크 데이터 전송에 이상적이다
  - Heap 영역에 데이터가 있으면,  JVM은 소켓을 통해 전송하기 전에 내부적으로 다이렉트 버퍼로 복사해야 한다.
- 힙 기반 버퍼보다 할당과 해제의 비용 부담이 크다

```java
ByteBuf directBuf = ...;
if (!directBuf.hasArray()) {
  int length = directBuf.readableBytes();
  byte[] array = new byte[length];
  directBuf.getBytes(directBuf.readerIndex(), array);
  handleArray(array, offset, length);
}
```

> Tip: 컨테이너의 데이터를 배열로 접근하는 것을 안다면, 다이렉트 버퍼보다 힙 버퍼를 사용하자



### 복합 버퍼

말 그대로 여러 버퍼가 병합된 가상의 하나의 버퍼를 말한다. - `CompositeByteBuf`

복합 버퍼를 사용하는 예)

- 헤더와 본문의 두 부분으로 구성되는 HTTP 메시지
  - 각각 다른 애플리케이션에서 생성된 메시지 본문을 조립하여 하나의 메시지로 전송 필요



*Netty 사용없이 JDK의 ByteBuffer를 이용하여 구현*

```java
ByteBuffer[] message = new ByteBuffer[] { header, body };
ByteBuffer message2 = ByteBuffer.allocate(header.remaining() + body.remaining());
message2.put(header);
message2.put(body);
message2.flip();
```

*Netty의 `CompositeByteBuf` 를 이용하여 구현*

```java
// 복합 버퍼 패턴
CompositeBytebuf messageBuf = Unpooled.compositeBuffer();
ByteBuf headerBuf = ...;
ByteBuf body = ...;
messageBuf.addComponents(headerBuf, bodyBuf);
...
messageBuf.removeComponent(0);
for (ByteBuf buf : messageBuf) {
  System.out.println(buf.toString());
}

// 데이터 접근
CompositeByteBuf compBuf = Unpooled.compositeBuffer();
int length = compuf.readableBytes();
byte[] array = new byte[length];
compBuf.getBytes(compBuf.readerIndex(), array);
handleArray(array, 0, array.length);
```



### 읽기와 쓰기

```
+------------------+----------------+------------------+
| 폐기할 수 있는 바이트 | 읽을 수 있는 바이트 | 기록할 수 있는 바이트 |
+------------------+----------------+------------------+
0 <-------------readerIndex <-----writerIndex <-------capacity


discardReadBytes() 호출 후
+------------------+-----------------------------------+
| 읽을 수 있는 바이트  |          기록할 수 있는 바이트         |
+------------------+-----------------------------------+
readerIndex<------writerIndex <-------------------capacity
```

- 일반 자바 바이트 배열과 비슷하다
  - 0 기반 인덱싱, 마지막 바이트는 capacity - 1

- `discardReadBytes()`

  - 이미 읽은 바이트를 폐기하고 기록할 수 있는 바이트의 크기를 증가시킨다
  - 자주 사용시 부담이 있다. - 읽을 수 있는 바이트를 버퍼의 시작 부분으로 옮기기 위해 메모리 복사가 실행됨

- read

  - `readBytes(ByteBuf dest);` 

  - ```java
    ByteBuf buffer = ...;
    while (buffer.isReadable()) {
      System.out.println(buffer.readByte());
    }
    ```

  - 읽을 수 있는 바이트를 다 읽은 뒤 읽으려고 하면 `IndexOutOfBoundsException`

- write

  - `writeBytes(ByteBuf dest);`

  - ```java
    ByteBuf buffer = ...;
    while (buffer.writeableBytes() >= 4) {
      buffer.writeInt(random.nextInt());
    }
    ```



### 파생 버퍼

- `duplicate()`
- `slice()`
- `slice(int, int)`
- `Unpooled.unmodifiableBuffer(...)`
- `order(ByteOrder)`
- `readSlice(int)`



### ByteBufHolder

- 실제 데이터 페이로드와 함께 다양한 속성 값을 저장해야하는 경우에 사용 ex) HTTP의 상태 코드, 쿠키 등



### 할당

#### 풀링된 버퍼

[ByteBufAllocator](https://netty.io/4.0/api/io/netty/buffer/ByteBufAllocator.html) 인터페이스를 이용하여 ByteBuf 인스턴스를 할당하는 데 이용할 수 있는 풀링을 구현한다.

```java
// ByteBufAllocator 참조 얻기
Channel channel = ...;
ByteBufAllocator allocator = channel.alloc();
....
ChannelHandlerContext ctx = ...;
ByteBufAllocator allocator2 = ctx.alloc();
```

#### 풀링되지 않는 버퍼

 [`Unpooled`](https://netty.io/4.1/api/io/netty/buffer/Unpooled.html) 라는 유틸리티 클래스를 제공하여 풀링되지 않는 ByteBuf 인스턴스 생성을 도와준다.



### ByteBufUtil

ByteBuf를 조작하기 위한 정적 도우미 메서드를 제공한다.

그 중에 `hexdump()` 라는 메서드는 ByteBuf의 내용을 16진수 표현으로 출력하게 해준다. - 로깅에 유용



### 참조 카운팅

- 다른 객체에서 더 이상 참조하지 않는 객체가 보유한 리소스를 해제해 메모리 사용량과 성능을 최적화하는 기법이다.

- Netty 4버전 부터  `ReferenceCounted` 인터페이스를 이용하여 Byte 와 ByteBufHodler에 참조 카운팅을 도입했다.
- PooledByteBufAllocator 와 같은 풀링 구현에서 메모리 할당의 오버헤드를 줄이는 데 반드시 필요하다.













 

