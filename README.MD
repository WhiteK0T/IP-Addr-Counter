## IP Addr Counter

Тестовая задача [Ecwid](https://github.com/Ecwid/new-job/blob/master/IP-Addr-Counter.md)
## Задача
Дан простой текстовый файл с IPv4 адресами. Одна строка – один адрес, примерно так:
```
145.67.23.4
8.34.5.23
89.54.3.124
89.54.3.124
3.45.71.5
```

Файл в размере не ограничен и может занимать десятки и сотни гигабайт.

Необходимо посчитать количество уникальных адресов в этом файле, затратив как можно меньше памяти и времени. 
Существует "наивный" алгоритм решения данной задачи (читаем строка за строкой, кладем строки в HashSet), 
желательно чтобы ваша реализация была лучше этого простого, наивного алгоритма.   


В качестве примера предлагается текстовый файл: [https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip\_addresses.zip](https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip). 
В распакованном виде этот файл занимает почти 107 Gb, в нём 8 миллиардов адресов, из которых один миллиард уникальных.

## Как создать и запустить проект

Для сборки приложения вы можете использовать эту команду:

```shell
mvn package 
```
Запустить программу можно командой:

```shell
java -jar target/UIAC.jar 6 "/путь/к/файлу/с/ip/адресами"
```
Обратите внимание, что для запуска приложения необходимо заменить `/путь/к/файлу/с/ip/адресами` на полный путь к исходному текстовому 
файлу с IPv4-адресами. А число 6 это количество потоков.

## Описание решения 

### Командная строка

В случае небольших текстовых файлов писать отдельную программу не обязательно, — для выполнения этой задачи мы можем 
использовать команды операционной системы. Например, в системе Linux команда будет выглядеть так:

```
sort -u ips.txt | wc -l
```

Но на больших файлах время выполнения будет очень значительным. В итоге вместо результата мы получим сообщение об ошибке:

```
sort: write failed: /tmp/sortcQjXmj: No space left on device 
0
```

### Наивное решение
С использование Stream API
```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class IPAddressCounter {

    public static void main(String[] args) {
        String filePath = "путь/к/вашему/файлу.txt";

        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            long uniqueIPCount = lines
                    .distinct()
                    .count();

            System.out.println("Количество уникальных IP-адресов: " + uniqueIPCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Обычное 
```java
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class IPAddressCounter {

    public static void main(String[] args) {
        String filePath = "путь/к/вашему/файлу.txt";
        Set<String> uniqueIPs = new HashSet<>();

        try (BufferedReader br = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                uniqueIPs.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Количество уникальных IP-адресов: " + uniqueIPs.size());
    }
}
```
Конфигурация i3 10100(4 ядра 8 потоков), Ram 16 гб, nvme 970 evo 
Итог запуска `Exception in thread "main" java.lang.OutOfMemoryError: Java heap space`

Так же, как и в случае с командной строкой, эти решения будет работать лишь на небольших объёмах данных.
Действительно, для работы программе нужно сохранить миллиард уникальных объектов-строк. На 64-разрядной JVM минимальный 
размер объекта равен 16 байтам, и для хранения миллиарда объектов потребуется минимум 16 Gb памяти. 
Если же мы говорим о конкретных строках, представляющих адреса, то их длина варьируется от 7 до 15 символов, и 
такие объекты будут занимать в памяти размер 24 или 32 байта. Понятно, что если это решение и будет работать, 
то только на очень мощном компьютере с большим объёмом памяти.

### Уменьшаем размер Set. Преобразуем строки в числа

**Первое**, что можно сделать для уменьшения объёма данных — преобразовать представление адреса из текстового в бинарный формат.
Интернет протокол четвёртой версии (IPv4) использует 32-битные (4-х байтные) адреса.

В нашем текстовом файле адреса записаны в виде четырёх десятичных чисел от 0 до 255, разделённых точками. 
Мы преобразуем каждое из таких чисел в байт и объединим их в целое число типа int. Таким образом каждый адрес будет 
занимать ровно 4 байта.

**Обычная реализация**
```java
public final class Util {

    private Util() {
    }

    public static long getLongFromIpAddress_parseInt(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        long result = 0;
        for (String octet : octets) {
            result = (result << 8) | Integer.parseInt(octet);
        }
        return result;
    }
}
```
**Stream Api**
```java
public class ConverterStringToInt implements ToIntFunction<String> {

    @Override
    public int applyAsInt(String ipAddress) {
        var octets = ipAddress.split("\\.");
        long result = 0;
        for (String octet : octets) {
            result = (result << 8) | Integer.parseInt(octet);
        }
        return (int) result;
    }
}
```

**Второе** нам нужно хранилище int-ов, так как в java все коллекции работают с обертками(Integer, Long и т.д.), а они нам 
не подходят из-за размера в памяти. **В добавок нам нужен беззнаковый int**. 

Следующий вопрос, который нужно решить — как именно сохранять информацию.

Если у нас порядка миллиарда чисел, каждое из которых занимает 4 байта, то для их хранения нам нужно 4Gb информации.
Но можно хранить не сами числа, а только информацию о том, есть ли такое число или нет. А для этого нам потребуется
всего один бит. Для целых чисел типа `int` нужно 2<sup>32</sup> бит, что равно 512Mb. Это значение фиксированное и
не зависит от общего количества чисел, которые нужно обработать.

В Java для работы с множеством битов есть класс `BitSet`, который вполне подходит для наших целей.

Учтём, что индекс бита может быть от `0` до `Integer.MAX_VALUE`, а нам требуется сохранять информацию не только
о положительных числах, но и об отрицательных. Поэтому мы создаём два сета, один для положительных чисел,
второй — для отрицательных. Вот простая реализация интерфейса для контейнера, в которой мы определяем два битовых сета:

```java
public class DualBitSet implements IntContainer {
    private final BitSet positive = new BitSet(Integer.MAX_VALUE);
    private final BitSet negative = new BitSet(Integer.MAX_VALUE);

    @Override
    public void set(int i) {
        if (i >= 0) {
            positive.set(i);
        } else {
            negative.set(~i);
        }
    }

    @Override
    public long countUnique() {
        return (long) positive.cardinality() + negative.cardinality();
    }
}
```


Теперь перепишем нашу программу с использованием конвертера и контейнера:

**Классическая реализация**

```java
import org.tehlab.whitek0t.dao.DualBitSet;
import org.tehlab.whitek0t.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class sample2 {

    public static void main(String[] args) {
        String filePath = "/mnt/dat200/ip_addresses";
        DualBitSet uniqueIPs = new DualBitSet();

        try (BufferedReader br = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                uniqueIPs.set((int) Util.getLongFromIpAddress_parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Количество уникальных IP-адресов: " + uniqueIPs.countUnique());
    }
}
```
Время выполнения на конфиге (i10100(4c,8t) + 16gb + nvme) с тестовым файлом в 107Gb = 00:19:12

**Stream Api реализация**

К сожалению, мы не можем использовать `distinct()`. Если мы посмотрим исходный код реализации, то увидите, что происходит
“упаковка” чисел с преобразованием в обычный стрим объектов, а затем вызов метода `distinct()` для обычного стрима. 
Если целое число занимает в памяти 4 байта, то обёртка в четыре раза больше - 16 байт.
Это происходит из-за того, что метод `distinct()` не имеет специализированной реализации для целых чисел. 

Для решения этой проблемы мы должны создать и использовать наш собственный, оптимизированный, контейнер для 
представления множества целых чисел.

#### Контейнер для чисел

Для начала определим, какой интерфейс должен быть у нашего контейнера. Добавим самый минимум методов, необходимый 
для решения нашей задачи.

Мы будем использовать метод `collect` целочисленного стрима, поэтому посмотрим на сигнатуру этого метода:

```
<R> R collect(Supplier<R> supplier,
              ObjIntConsumer<R> accumulator,
              BiConsumer<R,R> combiner)
```

Здесь `R` — тип изменяемого контейнера результатов. Параметр `supplier` создаёт (поставляет) наш контейнер, `accumulator`
принимает на вход контейнер и целое число, а `combiner` принимает на вход два контейнера и объединяет их. 
Последний используется, когда необходимо объединить паралельные стримы.

Так как мы будем использовать только последовательный стрим, то можно опустить реализацию метода `addAll`. Т
акже контейнеру понадобится метод, который вернёт количество собранных уникальных адресов. На основании всего 
вышеперечисленного определяем интерфейс для нашего контейнера:

```java
public interface IntContainer {
    // accumulator
    void add(int number); 

    // combiner
    default void addAll(IntContainer other) {
        throw new UnsupportedOperationException();
    }

    long countUnique();
}
```

Теперь подсчёт уникальных адресов будет выглядеть следующим образом:

```java
import org.tehlab.whitek0t.dao.DualBitSet;
import org.tehlab.whitek0t.codeForReadme.IntContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class sample2 {
    public static void main(String[] args) {
        String filePath = "/mnt/dat200/ip_addresses";
        ConverterStringToInt converterStringToInt = new ConverterStringToInt();

        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            long uniqueIPCount = lines
                    .mapToInt(converterStringToInt)
                    .collect(DualBitSet::new, IntContainer::set, IntContainer::addAll)
                    .countUnique();

            System.out.println("Количество уникальных IP-адресов: " + uniqueIPCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Когда мы запустим наше приложение, программа, хоть и небыстро, но успешно прочитает и 
обработает тестовый файл и выдаст правильный результат: 1,000,000,000 уникальных адресов.

Время выполнения на конфиге (i10100(4c,8t) + 16gb + nvme) с тестовым файлом в 107Gb = 00:19:23

### Оптимизируем конвертер

Вернёмся к алгоритму конвертации адреса из текстового представления в число. Мы взяли готовый алгоритм из интернета. 
Но насколько он хорош? К сожалению, как и многие готовые решения из интернета, этот код далёк от оптимального.

Рассмотрим эту строчку:

```java
String[] ipAddressInArray = ipAddress.split("\\.");
```

Основная проблема с производительностью в том, что здесь создаются пять новых объектов — четыре строки и массив. 
Эти новые объекты используются только в пределах метода и после его завершения должны быть собраны уборщиком мусора. 
Создание объекта — затратная операция. Виртуальная машина должна выделить и инициализировать память для этих объектов, 
а после работы метода эти объекты остаются в памяти до тех пор, пока не будут обработаны сборщиком мусора.

Вторая строка, на которую нужно обратить внимание, это преобразование строк в числа:

```java
int ip = Integer.parseInt(ipAddressInArray[i]);
```

Здесь метод `Integer::parseInt` проводит проверку входных данных и может выкинуть исключение `NumberFormatException`.

К сожалению, это не может считаться проверкой адреса на правильность, так как метод пропускает все числа за пределами
допустимого диапазона от `0` до `255`. Если у нас стоит задача проверять IP адреса, то можно использовать 
класс `InetAddress`. Например, мы можем конвертировать адрес с помощью такого кода:

```java
String ipAddress = "192.168.2.1";

int ip = ByteBuffer.allocate(Integer.BYTES)
            .put(InetAddress.getByName(ipAddress).getAddress())
            .getInt(0);
```

В случае некорректного адреса метод `InetAddress::getByName` выкидывает проверяемое исключение `UnknownHostException`, 
которое мы должны перехватить и обработать.

Однако, по условию задачи, все адреса корректные. Нам не нужно проводить дополнительную проверку их правильности.

Что нужно, чтобы написать оптимальный метод? Во-первых, мы должны избегать создания ненужных временных объектов. 
Во-вторых, нам не нужно использовать сложные встроенные методы для парсинга чисел, если мы можем написать собственный 
простой алгоритм. Попробуем написать конвертер, удовлетворяющий этим условиям:

**Stream Api реализация**

```java
public class OptimizedConverter implements ToIntFunction<CharSequence> {

    @Override
    public int applyAsInt(CharSequence ipAddress) {
        int base = 0;
        int part = 0;

        for (int i = 0, n = ipAddress.length(); i < n; ++i) {
            char symbol = ipAddress.charAt(i);
            if (symbol == '.') {
                base = (base << Byte.SIZE) | part;
                part = 0;
            } else {
                part = part * 10 + symbol - '0';
            }
        }
        return  (base << Byte.SIZE) | part;
    }
}
```

**Классическая реализация**

```java
public static long getLongFromIpAddress_Optimized(CharSequence ipAddress) {
    int base = 0;
    int part = 0;
    char symbol;

    for (int i = 0, n = ipAddress.length(); i < n; i++) {
        symbol = ipAddress.charAt(i);
        if (symbol != 13) {
            if (symbol == '.') {
                base = (base << Byte.SIZE) | part;
                part = 0;
            } else {
                part = part * 10 + symbol - '0';
            }
        }
    }
    return ((long) base << Byte.SIZE) | part;
}
```
В этом алгоритме мы избегаем создания объектов и не используем “тяжёлые” методы для конвертации текста в число. 

### Сравнение конвертеров

Давайте посмотрим, насколько у нас получилось улучшить быстродействие алгоритма. Для этого мы используем фреймворк JMH.
Результаты замера:

```
Benchmark                                  Mode  Cnt   Score   Error  Units
UtilTest.getLongFromIpAddress_InetAddress  avgt    3  24.442 ± 0.992  ns/op
UtilTest.getLongFromIpAddress_Optimized    avgt    3   0.366 ± 0.033  ns/op
UtilTest.getLongFromIpAddress_parseInt     avgt    3  88.273 ± 8.072  ns/op

```
Как видим, наша собственная реализация работает на порядок быстрее.

### Оптимизируем контейнер

Реализация контейнера, использующая два `BitSet`, является простой и достаточно эффективной. Однако мы можем немного 
улучшить эту реализацию. Универсальный класс `BitSet` содержит различные проверки, которые мы можем исключить в 
нашей собственной реализации.

В качестве основы реализации мы используем массив целых чисел типа `long`. Так как этот тип занимает в памяти 8 байт 
или 64 бита, мы можем использовать одно такое число в качестве сета для 64 чисел. 
Ниже представлен пример реализации контейнера, основанный на массиве:

```java
public class BitSetArray implements IntContainer {
    private final long[] bits;
    private final long size;

    public BitSetArray() {
        this.size = (long) Integer.MAX_VALUE << 1;
        long arraySize = (long) Math.ceil((double) size / 64);
        bits = new long[(int) arraySize];
    }

    @Override
    public void set(int i) {
        long index = i & 0xFFFFFFFFL;
        if (index >= size) {
            throw new IndexOutOfBoundsException("Index out of range. index = " + index);
        }
        int arrayIndex = (int) (index / 64);
        int bitIndex = (int) (index % 64);
        bits[arrayIndex] |= (1L << bitIndex);
    }

    public boolean get(int i) {
        long index = i & 0xFFFFFFFFL;
        if (index >= size) {
            throw new IndexOutOfBoundsException("Index out of range. index = " + index);
        }
        int arrayIndex = (int) (index / 64);
        int bitIndex = (int) (index % 64);
        return ((bits[arrayIndex] >> bitIndex) & 1) == 1;
    }

    @Override
    public long countUnique() {
        long sum = 0;
        for (long bit : bits) sum += Long.bitCount(bit);
        return sum;
    }
}
```

Данная реализация хорошо подходит для случая, когда у нас большое количество IPv4 адресов, случайно распределённых на 
всём возможном диапазоне. Недостаток этой реализации в том, что мы сразу выделяем 512Мб памяти.


Время выполнения стандартной реализации на конфиге (i10100(4c,8t) + 16gb + nvme) с тестовым файлом в 107Gb = 00:07:34

Время выполнения Stream API реализации на конфиге (i10100(4c,8t) + 16gb + nvme) с тестовым файлом в 107Gb = 00:07:15

### Переход на Java NIO

Дальнейший рост производительности упирается в нехватку входных данных. Современные накопители могут читать данные
с очень большой скоростью. Например: HDD читает около 200 мегабайт в секунду, SSD около 500 Mb, Nvme накопители от 3 до 10 
Gb в секунду (PCIe3 до 3.5Gb, PCIe4 до 7.5Gb, PCIE5 около 10Gb). К тому же данные могут быть на сетевом диске.
На данный момент у нас чтение происходит со скоростью ~245Mb.
Будем продолжать оптимизировать код.  

Воспользуемся **FileChannel** и **ByteBuffer**.

**FileChannel** - это класс в Java, который предоставляет возможность чтения, записи и манипулирования файлами ввода-вывода.
Он является частью пакета `java.nio.channels` и предоставляет более гибкий и эффективный способ работы с файлами, 
чем классы ввода-вывода (`InputStream и OutputStream`) из пакета `java.io`.

**FileChannel** позволяет осуществлять операции чтения и записи с использованием буфера данных, таких как **ByteBuffer**. 
Он также поддерживает операции перемещения позиции в файле, изменения размера файла, блокировки файла и другие операции,
связанные с файловым вводом-выводом.

**ByteBuffer** - это класс в Java, который представляет собой буфер для работы с последовательностью байтов. Он является 
частью пакета `java.nio` и предоставляет удобные методы для чтения и записи байтов.

**ByteBuffer** предоставляет возможность управления буфером и выполнять различные операции, такие как чтение, запись, 
преобразование и перемещение данных. Он может быть использован для обработки бинарных данных, работы с сетевыми 
протоколами, чтения и записи файлов и других операций, связанных с байтами.

```Java
public class UniqueIpAddressCounter_NIO implements Worker {

    private static final ArrayBitSet arrayBitSet = new ArrayBitSet((long) Integer.MAX_VALUE << 1);

    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        long uniqueAddresses = 0;
        LocalTime leadTime = LocalTime.MIN;
        long numberOfLines = 0;

        try (FileChannel fileChannel = FileChannel.open(filePath)) {
            long fileSize = fileChannel.size();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1 << 27); // Размер буфера
            LocalTime startTime = LocalTime.now();
            long bytesRead = 0;
            int baseNum = 0;
            int partNum = 0;

            while (bytesRead < fileSize) {
                buffer.clear();
                bytesRead += fileChannel.read(buffer);
                buffer.flip();
                int symbol;
                while (buffer.hasRemaining()) {
                    symbol = buffer.get();
                    if (symbol == 13) {
                        continue;
                    }
                    if (symbol == 10) {
                        bitArraySet.set(((long) baseNum << Byte.SIZE) | partNum);
                        baseNum = 0;
                        partNum = 0;
                        numberOfLines++;
                    } else {
                        if (symbol == '.') {
                            baseNum = (baseNum << Byte.SIZE) | partNum;
                            partNum = 0;
                        } else {
                            partNum = partNum * 10 + symbol - '0';
                        }
                    }
                }
                consumer.accept(bytesRead);
            }
            uniqueAddresses = bitArraySet.cardinality();
            LocalTime endTime = LocalTime.now();
            leadTime = endTime.minusNanos(startTime.toNanoOfDay());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Result(uniqueAddresses, numberOfLines, leadTime);
    }
}
```
Время выполнения на конфиге (i10100(4c,8t) + 16gb + nvme) с тестовым файлом в 107Gb = 00:04:57

Время выполнения на конфиге (7950x(16c,32t) + 64gb + nvme pcie4) с тестовым файлом в 107Gb = 00:04:41

### MultiThread

NIO может больше. Но у нас медленная обработка данных, а что если ее раскидать по потокам.
Вот многопоточная реализация

```java
public class UniqueIpAddressCounter_NIO_MultiThreads implements Worker {
    public static int capacity = 1 << 27; //  134_217_728
    private final static ArrayBitSet arrayBitSet = new ArrayBitSet((long) Integer.MAX_VALUE << 1); // 4_294_967_294
    private final List<BufferHandler> bufferHandlers = new ArrayList<>();
    private long filePartSize = 0;

    @Override
    public Result work(Path filePath, int numberOfThreads, Consumer<Long> consumer) {
        long uniqueAddresses = 0;
        long numberOfLines = 0;
        LocalTime leadTime = LocalTime.MIN;
        try (FileChannel fileChannel = FileChannel.open(filePath)) {
            long fileSize = fileChannel.size();
            init(numberOfThreads, fileSize);
            LocalTime startTime = LocalTime.now();
            boolean firstRun = true;
            while (this.bufferHandlers.get(0).currenPos <= filePartSize) {
                for (int i = 0; i < this.bufferHandlers.size(); i++) {
                    BufferHandler bufferHandler = this.bufferHandlers.get(i);
                    ByteBuffer byteBuffer = bufferHandler.buffer;
                    bufferHandler.semaphore.acquire();
                    byteBuffer.clear();
                    sliceBufferIfCapacityExceeded(fileSize, i, bufferHandler, byteBuffer);
                    bufferHandler.currenPos += fileChannel.read(byteBuffer, bufferHandler.currenPos);
                    byteBuffer.flip();
                    firstRun = isFirstRun(firstRun, i, bufferHandler, byteBuffer);
                    bufferHandler.semaphore.release();
                }
                consumer.accept(this.bufferHandlers.get(0).currenPos);
            }
            Thread.sleep(1500);
            numberOfLines = getNumberOfLinesAndStopThreads(numberOfLines);
            uniqueAddresses = bitArraySet.cardinality();
            LocalTime endTime = LocalTime.now();
            leadTime = endTime.minusNanos(startTime.toNanoOfDay());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Result(uniqueAddresses, numberOfLines, leadTime);
    }

    private long getNumberOfLinesAndStopThreads(long numberOfLines) {
        for (BufferHandler bufferHandler : this.bufferHandlers) {
            bufferHandler.interrupt();
            numberOfLines += bufferHandler.numberOfLines;
        }
        return numberOfLines;
    }

    private void sliceBufferIfCapacityExceeded(long fileSize, int i, BufferHandler bufferHandler, ByteBuffer byteBuffer) {
        long endFilePart = bufferHandler.calculatedPos + this.filePartSize;
        if (bufferHandler.currenPos + capacity > endFilePart) {
            long newSizeBuf = getNewSizeBuf(fileSize, i, bufferHandler);
            bufferHandler.buffer = byteBuffer.slice(0, (int) newSizeBuf);
        }
    }

    private void init(int numberOfThreads, long fileSize) {
        this.filePartSize = fileSize / numberOfThreads;
        if (capacity > filePartSize) {
            capacity = (int) (filePartSize / 2);
        }
        long curPart = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity);
            BufferHandler bufferHandler = new BufferHandler(byteBuffer, new Semaphore(1));
            bufferHandler.calculatedPos = curPart;
            bufferHandler.currenPos = curPart;
            bufferHandler.startPos = curPart;
            this.bufferHandlers.add(bufferHandler);
            curPart += this.filePartSize;
        }
    }

    private long getNewSizeBuf(long fileSize, int index, BufferHandler bufferHandler) {
        long newSizeBuf;
        if (index != this.bufferHandlers.size() - 1) {
            newSizeBuf = this.bufferHandlers.get(index + 1).startPos - bufferHandler.currenPos;
        } else {
            newSizeBuf = fileSize - bufferHandler.currenPos;
        }
        return newSizeBuf;
    }

    private boolean isFirstRun(boolean firstRun, int index, BufferHandler bufferHandler, ByteBuffer byteBuffer) {
        if (firstRun) {
            // поиск и сохранение стартовой позиции в буфере
            if (index != 0) {
                int symbol;
                while (byteBuffer.hasRemaining()) {
                    symbol = byteBuffer.get();
                    bufferHandler.startPos++;
                    if (symbol == 10) {
                        break;
                    }
                }
            }
            if (index == this.bufferHandlers.size() - 1) {
                firstRun = false;
            }
            bufferHandler.setDaemon(true);
            bufferHandler.start();
        }
        return firstRun;
    }

    class BufferHandler extends Thread {

        private long calculatedPos = 0; //посчитанная стартовая позиция
        private long currenPos = 0; // текущая позиция
        private long startPos = 0; // позиция от первого переноса строки "\n"
        private ByteBuffer buffer;
        private final Semaphore semaphore;
        private int baseNum = 0;
        private int partNum = 0;
        private long numberOfLines = 0;  // количество обработанных строк

        public BufferHandler(ByteBuffer buffer, Semaphore semaphore) {
            this.buffer = buffer;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            int symbol;
            while (!this.isInterrupted()) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    //throw new RuntimeException(e);
                    //TODO: решить проблему с InterruptedException, семафор периодически ловит
                }
                while (buffer.hasRemaining()) {
                    symbol = buffer.get();
                    if (symbol == 13) {
                        continue;
                    }
                    if (symbol == 10) {
                        bitArraySet.set(((long) baseNum << Byte.SIZE) | partNum);
                        baseNum = 0;
                        partNum = 0;
                        numberOfLines++;
                    } else {
                        if (symbol == '.') {
                            baseNum = (baseNum << Byte.SIZE) | partNum;
                            partNum = 0;
                        } else {
                            partNum = partNum * 10 + symbol - '0';
                        }
                    }
                }
                semaphore.release();
            }
        }
    }
}
```
####  Конфигурация (i10100(4c,8t) + 16gb + nvme pcie3) с тестовым файлом в 107Gb:
Время выполнения в 6 потоков = 00:01:21

Сравнение разных реализаций (Threads = 6 для uniqueIpAddressCounterNioMultiThreads)
```
Benchmark                                          Mode  Cnt  Score   Error   Units
WorkerBench.uniqueIpAddressCounterBufferedReader     ss       7.550          min/op
WorkerBench.uniqueIpAddressCounterLines              ss       7.242          min/op
WorkerBench.uniqueIpAddressCounterNio                ss       4.678          min/op
WorkerBench.uniqueIpAddressCounterNioMultiThreads    ss       1.391          min/op
```

####  Конфигурация (7950x(16c,32t) + 64gb + nvme pcie4) с тестовым файлом в 107Gb:
Время выполнения в 8 потоков = 00:00:44
Дальнейшее увеличение потоков на amd не давало прироста: 12 потоков и результат 42 секунды.

Сравнение разных реализаций (Threads = 6 для uniqueIpAddressCounterNioMultiThreads)
```
WorkerBench.uniqueIpAddressCounterBufferedReader     ss       4,079          min/op
WorkerBench.uniqueIpAddressCounterLines              ss       4,246          min/op
WorkerBench.uniqueIpAddressCounterNio                ss       3,368          min/op
WorkerBench.uniqueIpAddressCounterNioMultiThreads    ss       0,856          min/op
```
Довел код до консольного приложения и теперь оно выглядит так: 
```
#java -jar UIAC.jar 6 /mnt/dat200/ip_addresses 
Program for counting unique IP addresses.
UIAC version 1.0   By WhiteK0T  https://github.com/WhiteK0T/IP-Addr-Counter
UIAC [number of threads 1-20] filename with ip addresses
Threads : 6
Path : /mnt/dat200
File : ip_addresses
Size : 107Gb
Worker : UniqueIpAddressCounter_NIO_MultiThreads

Done: 100%

Lead time: 00:01:20.315875265
Number of IP addresses in the file: 8000000000

Number of unique IP addresses: 1000000000

```
_Полный код доступен в репозитории по адресу:_  
[_https://github.com/WhiteK0T/IP-Addr-Counter_](https://github.com/WhiteK0T/IP-Addr-Counter)