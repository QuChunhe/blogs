分布式ID的应用和考量
====================
屈春河
--------------------




# 基础知识

在关系型数据库中采用INT类型（32位整数）或者BIGINT类型（64位整数）作为表的主键是一种常见的做法，这种主键也常常被命名为ID（***_ID）。最为简单的一种ID生成方式是自增长，即在定义表结构时采用关键字AUTO_INCREMENT[1]和NOT NULL修饰ID，那么在插入一行数据时，如果ID为空，则数据库会自动采用MAX(ID)+1(在MySQL中根据innodb_autoinc_lock_mode配置的不同，生成的ID可能并不连续)作为该行数据的主键。上述ID生成方式虽然简单，但是无法适用于如下的分布式环境
* 分布式数据库集群，即多个数据库实例以Shared-Nothing方式组成数据库集群。这使得采用AUTO_INCREMENT方式生成的ID会在不同的数据库中产生重复的ID。
* 分布方式插入数据，即存在多个应用相互独立地插入数据。为此，需要一种方案或者机制以确保在不同应用插入数据时每行数据的ID是唯一的。



针对于上述分布式环境的ID生成方案也被称为分布式ID生成方案，其实现非常多，归纳起来可以划分如下两大类：
* 集中协调方案，即通过集中式的服务来协调各个应用生成ID或者分配ID，以实现ID的唯一性，例如使用Spider存储引擎，可以设置spider_auto_increment_mode[2]为1，能够兼容AUTO_INCREMEN方式实现自增长ID。
* 规则划分方案，即通过特定的规则对于可用ID进行划分，使得每个应用以排他方式占用其中一个划分。最为知名一个例子是Twitter Snowflake方案，其通过中间10个bit的工作机器ID划分ID，使得ID并不会重复。

在本质上集中协调方案是一种针对分布式环境的集中式ID生成方案，其实现相对简单，但是性能往往不如基于规则划分的分布式方案。

虽然分布式ID生成方案很多，但是在实际应用时需要从如下几个方面评估和选择：
* 有效性。满足ID的唯一性，不能存在重复的ID。
* 高效性。高效性主要涵盖如下几个指标：a）生成效率，即ID生成速度，尤其对于集中协调方案制而言，要避免生成ID成为插入数据的性能瓶颈；b）资源效率，ID是32位或64位整数，要充分利用这些整数资源，避免大范围弃之不用而造成ID不够用（ID溢出）的问题；c）使用效率，要结合存储引擎和查询模式，优化数据存储效率以及应用查询效率。
* 简单性。在满足有效性和高效性的前提下，实现要尽可能的简单，以降低开发和集成的复杂性。


针对于上述的评估指标，在下文中将会举两个实际的例子，来说明如何根据业务需求和实际情况，选择和设计分布式ID方案。


# 例1—日志型数据入库

日志型数据指的是那些一旦生成就不会被更改的数据，比如用户访问日志等。这些数据生成之后，会被实时地发送到Kafak集群。根据实际部署情况，Kafka集群可能是一个或者多个，而topic也可能是一个或者多个。需要指出的是如果是一个Kafka集群并且是一个topic，那么需要将topic配置为多个Partition，而Kafka Client则需要采用相同的group.id，从而实现多个Kafka Client以协同方式同时从一个topic获取消息。

<img src="https://github.com/QuChunhe/blogs/blob/master/pic/2020-06-14_demo-1.png" width="1000"  alt="图-1 日志型数据入库示意图" title="图-1 日志型数据入库示意图"/><br/>

![图-1 日志型数据入库示意图](https://github.com/QuChunhe/blogs/blob/master/pic/2020-06-14_demo-1.png) 图-1 日志型数据入库示意图


如图1所示，多个Kafka Client以分布方式入库数据，整个入库过程包含如下几个功能步骤：
1. 数据获取，从Kafka集群读取消息。
2. 数据整理。根据业务需求，对于读取的数据进行规范化。
3. 数据插入。将规范化后的数据插入一个或者多个日志表中。如果插入多个表，往往采用相同的ID以相互关联。
4. 数据汇总。插入/更新不同维度或者不同粒度的汇总表，用以支持相关的统计分析功能。

分布式ID主要针对于数据插入，即在插入原始日志表时需要生成唯一的ID作为该表的主键。


针对此种需求，最为简单的方案是采用Spider引擎[\cite{spider2}]和自增长ID。作为一种集中式ID协调机制，此种方案的实现和使用都非常简单。然而，当数据规模非常庞大时，此种方案的查询效率非常低。一种常见的补充方案是定期地将数据转移到历史表中，例如以年为周期转移到历史表****\_yyyy或者以月为周期转移到历史表****\_yyyy\_mm。通过历史表，虽然能够在一定程度上改善查询效率，却增加了查询的复杂性，需要在查询语句中显示地指定查询哪个历史表，甚至于如果数据分布在多个历史表中，则不仅需要查询多个历史表，而且还要对查询结果进行UNION操作。为了优化数据查询，下文会介绍一种基于Twitter Snowflake的改良方案，并结合分区[\cite{partition}]，从而既可以获得远超自增长ID方案的查询性能，又无需在查询中显示地指定历史表。

如图-2所示，64位ID被划分为三个部分：第一部分，前32位为Unix时间戳，其为从格林威治时间1970年1月1日00点00分00秒到当前的总秒数；第二部分，中间n位代表服务ID，可以根据需要调整n的大小；第三部分，后32-n位为自增长整数，当32-n位整数用尽时会自动归零并且从零开始增加。显然，为不同的应用分配不同的服务ID，可以确保ID不会相同。

\begin{figure}[htbp]
\label{fig:demo-1-id}
  \centering
  \includegraphics[width=0.75\textwidth]{2020-06-14_id.png}
  \caption{ID示意图}
\end{figure}

要根据实际情况，合理选择n的大小。如果n太大，一旦每秒产生的数据量超过$2^{32-n}$，就会造成ID重复。如果n太小，会限制Kafka Client的数量，当入库操作比较耗时并且数据量比较庞大时，会导致数据积压，数据无法及时入库。具体而言，n的取值跟如下几个因素有关：
\begin{itemize}
  \item 应用的数量a，即在图-1中Kafka Client的数量，显然，$a<=2^n$。
  \item 数据的峰值m，即每秒最多产生多少个消息，也就是说，每秒需要多少不同的ID。
  \item 数据的均值q，即每秒平均产生多少个消息。
  \item 处理的耗时t，即平均每个数据的入库时间，可以根据测试获得此值。
  \item 应用并发度k，即每个应用使用多线程以并发方式处理数据入库操作。Kafka Client在获取数据后采用线程池，以一个线程处理一个消息的方式完成步骤2)到4)的功能，其并发度k约等于线程池的最大可用线程数。
\end{itemize}

上述因素中，m和q根据当前实际情况和未来业务规划进行估算，而a和k的取值则依赖于配置或者部署情况。受限于每秒生成的ID数量，每个应用每秒最多处理$2^{32-n}$个数据。由于应用数量可能少于Kafka Partition数量或者消息在各个Kafka Partition之间可能不平衡，因此需要满足$m/a<<2^{32-n}$，以留出充足的余量。此外，每个应用每秒最多处理k/t个数据，需要满足$a*k/t>q$，即$2^n*k/t>q$，以确保数据能够及时入库。根据上述两个关系，可以大概估计出n的取值范围为$\log_2(q*t/k)<n<<32-\log_2(m/a)$。

合理使用数据库分区(Partition)[\cite{partition}]，能够大大地减小查询时间。为了充分发挥分区的性能优势，需要满足如下两个条件：
\begin{itemize}
  \item 查询条件中包含分区条件的约束，即根据查询条件就能确定数据所在分区。
  \item 查询条件所确定的分区数量不多，即所查询数据分布在不多的几个分区内。
\end{itemize}
针对于日志型数据的应用往往查询特定时间范围内的数据，而在图-2中ID的前32位（ID>>32）代表Unix时间戳。这意味着根据ID范围划分分区并根据ID范围进行查询，可以优化数据查询效率。依据数据规模，可以以年或月或周划分分区。如下SQL实例中，以自然月为周期定义分区，例如6448965550394572800对应于时间“2017-08-01 00:00:00”，而 6460469190800179200对应于时间“2017-09-01 00:00:00”。

\begin{lstlisting}[framerule=0pt,escapeinside=``]
CREATE TABLE **** (
  id BIGINT UNSIGNED NOT NULL,
  ... 
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
  PARTITION BY RANGE (id) (
  PARTITION pmin VALUES LESS THAN (6437461909988966400),
  PARTITION p201707 VALUES LESS THAN (6448965550394572800),
  PARTITION p201708 VALUES LESS THAN (6460469190800179200),
  ...
  PARTITION pmax VALUES LESS THAN MAXVALUE );
\end{lstlisting}


针对于上述的分区，在WHERE查询条件中必需添加ID范围约束，如下SQL实例所示。因为InnoDB对于主键采用聚簇索引，根据主键范围能够非常快速地读取所需数据。因此，通过本方案能够减小不必要的数据扫描，快速地定位到所需数据，从而大大减小了查询所需时间。

\begin{lstlisting}[framerule=0pt,escapeinside=``]
  SELECT ..
  FROM ...
  WHERE (id>=(UNIX_TIMESTAMP('2020-08-05 08:00:00')<<32)) AND
        (id< (UNIX_TIMESTAMP('2020-08-06 08:00:00')<<32)) AND 
        ...
\end{lstlisting}

上述方案的一个潜在问题是32位Unix时间戳的溢出。如果系统需要持续运行数十年的时间，那么ID的前32位将会在格林威治时间2038年01月19日03时14分07秒溢出，即无法用32位无符号整数表示Unix时间戳。为了防止这种情况的发生，可以采用相对Unix时间戳，即ID的前32位保存从近期一个特定时间开始到当前时间的总秒数，例如从格林威治时间2020年1月1日00点00分 00秒到当前的总秒数，其可以方便地通过当前时间的UNIX时间戳减去UNIX\_TIMESTAMP('2020-01-01 00:00:00')得到。

\section{例2—跨数据中心数据同步}


跨数据中心数据同步是针对同时满足如下约束的业务场景。
\begin{itemize}
  \item 多个数据中心同时写入数据，即位于不同数据中心的应用都需要向相同的表写入数据。
  \item 不更改数据或者所更改的数据集互不相交，也就是说，位于不同数据中心的应用即使更改同一个表，但是所更改的数据不同。
  \item 每个数据中心的应用都需要读取全部数据。
\end{itemize}
针对上述业务场景，下文将会介绍一种基于AUTO\_INCREMENT自增长ID和Kafka消息队列的方案。需要说明的是，采用例1所示的ID生成方式也是一种可选方案，其中不同的服务ID对应于不同的数据中心，但是例1中的方案将ID生成推给应用，不仅增加了应用的复杂性，而且有些情况下还难以实现，比如对于PHP应用，难于协同多个PHP进程（请求）生成ID。此外，如果多个数据中心需要更改相同的数据，在一些情况下也能够通过补充方案进行支持，但是无法支持分布式事务。


图-\ref{fig:demo-2}所示为在两个数据中心之间同步数据的功能示意图。对于多个数据中心的情况，类似于图-\ref{fig:demo-2}需要每个数据中心对应一个topic。对于一个数据中心而言，其一方面将本地数据中心的数据发送到对应的topic，另一个方面从其他topic获取消息并插入到本地数据库。


\begin{figure}[htbp]
  \centering
  \includegraphics[width=\textwidth]{2020-06-14_demo-2.png}
  \caption{跨数据中心数据同步示意图}
  \label{fig:demo-2}
\end{figure}

对于AUTO\_INCREMENT修饰的自增长主键，MySQL提供了两个系统变量用于支持源和源之间(source-to-source)的复制：auto\_increment\_increment和auto\_increment\_offse[\cite{autoincrement}]。上述两个系统变量分别定义了自增长主键的初始值和增加步长。如果在N个数据中心之间同步数据，那么配置auto\_increment\_offset=N并且针对不同数据中心分别配置auto\_increment\_increment为1, 2, ..., N。通过上述配置，N个数据库实例中的自增长主键将不会重复。此外，还需要将MySQL系统变量binlog\_format配置为row。


在图-\ref{fig:demo-2}中，Binlog监听功能采用Binlog Connector[\cite{binlog2}]连接MySQL Server，其在本质上充当了MySQL Server的Slave，能够实时地从MySQL Server获取Binlog的插入(INSERT)和更新(UPDATE)日志，并进一步将日志解析和转化为消息，然后将消息插入到特定的Kafka topic。数据写入功能实时地获取Kafka消息，然后将消息转化为对应的SQL语句并依次逐个逐个地执行，从而将数据写入到数据库中。

为了实现数据同步的正确性，还需要解决如下两个问题
\begin{itemize}
  \item 确保操作的时序性。在执行SQL写入数据时，写入操作要按照Binlog中的顺序依次执行。例如，在Binlog中如果操作$op_1$位于操作$op_2$的前面，那么在写入数据库时要确保操作$op_2$开始执行的时间一定不能早于操作$op_1$执行完毕的时间，即只有一个写入操作执行完毕后，才能开始执行后续写入操作。
  \item 避免操作循环同步。根据写入操作的来源，可以将写入操作划分为两类，一类是来自本地应用的本地操作，另一类是来自于Kafka消息的异地操作。MySQL Server无法区分上述两类操作，因此这两类都会被写入Binlog。如果Binlog监听功能不加区分，这些异地操就会再次被同步到其他数据库中心，造成写入操作消息在数据中心之间来回往返的传递，甚至形成操作消息风暴。
\end{itemize}


从两个方面来解决操作时序性问题。第一，确保消息在Kafka Server中的存储顺序（offset顺序）与对应操作在Binlog中的顺序相同。为此，采用单
线程执行Binlog监听功能，并且增加如下配置[\cite{kafka}]，以保证Kafka Producer依照顺序发生消息。第二，确保依照消息的存储顺序执行对应的SQL语句。为此，topic的Partition要设置为1，并且数据写入功能采用单线程，即使用一个线程顺序执行如下操作：读取消息，依照消息顺序逐个逐个地解析消息并执行写入操作。
\begin{lstlisting}[framerule=0pt,escapeinside=``]
  acks=all
  max.in.flight.requests.per.connection=1 
\end{lstlisting}

对操作循环同步问题，则采用基于Guava Cache的过滤功能，过滤掉异地操作。在执行SQL语句插入数据之前，需要将操作缓存到Cache中。如果为INSERT操作，则Cache key为依照字典排序的主键，例如主键分别为k1,k2,...,kn，对应的key则为k1=v1\&k2=v2\&...kn=vn，而对应的Cache value则为AtomicInteger(1)。如果为UPDATE操作，则Cache key可以分为两个部分，前一部分是依照字典排序的主键，后一部分是依照字典排序的更新列，例如主键分别为k1,k2,...,kn，更新的列分别为c1,c2,...,cm，对应的key则为k1=v1\&k2=v2\&...\&kn=vn\&c1=w1\&c2=w2\&...cm=wm，而对应的Cahce value取值，还需要判断Cache中是否已经存在此key：如果key不存在，则value直接设置为AtomicInteger(1)；否则将Cache中已经缓存的value加1。Binlog监听功能在获得写入操作（MySQL写入事件）后，需要根据上述规则获得对应的Cache key，并且判断key在Cache中是否存在：如果不存在，则对应的操作为本地操作，需要发送到Kafka消息队列；如果存在，则将对应的value减1，然后判断value是否为0，如果为0，则将此key/value对从Cache中删除。

\begin{lstlisting}[framerule=0pt,escapeinside=``]
    public void initialize() {
        Cache<String, AtomicInteger>
                 factory = CacheBuilder.newBuilder()
                                       .softValues()       
                                       .expireAfterWrite(expireSecondsAfterWrite,TimeUnit.SECONDS)
                                       .build();
        cache = factory.asMap();
    }


    private String toKey(MysqlWriteRow event) {
        List<Column> primaryKeys = event.primaryKeys();
        Column[] sortedPrimaryKeys = primaryKeys.toArray(new Column[primaryKeys.size()]);
        Arrays.sort(sortedPrimaryKeys, COLUMN_COMPARATOR);
        StringBuilder builder = new StringBuilder(128);
        builder.append(event.table())
               .append(":")
               .append(event.type())
               .append(":");
        for(Column c : sortedPrimaryKeys) {
            builder.append(c.name())
                   .append("=")
                   .append(c.value())
                   .append("&");
        }
        if (MysqlEvent.Type.UPDATE.toString().equals(event.type())) {
            List<Column> row = event.row();
            Column[] sortedColumns = row.toArray(new Column[row.size()]);
            Arrays.sort(sortedColumns, COLUMN_COMPARATOR);
            for(Column c : sortedColumns) {
                builder.append(c.name())
                       .append("=")
                       .append(c.value())
                       .append("&");
            }
        }
        return builder.substring(0, builder.length()-1);
    }

    private final ColumnComparator COLUMN_COMPARATOR = new ColumnComparator();

    private ConcurrentMap<String, AtomicInteger> cache;
    private long expireSecondsAfterWrite = 600; 
\end{lstlisting}


如果各个数据中心所更改的数据集有重合，可以通过补充方案支持一些特殊的情况。补充方案1：将有重合的更改操作集中到一个数据中心，并以服务的形式向外提供更改功能；其他数据中心的应用或者通过基于Web Service的同步调用或者通过基于消息队列的异步调用来请求此服务。补充方案1的问题是时延非常大，如果应用需要及时地获得更新数据，以执行后续操作，那么补充方案1就无法满足。因为我们的业务场景更加特殊，仅仅有一个表（为了方便起见，表名称为resource）的数据需要同时更改，即需要更新这个表的对应列，关联和去关联其他表的，实现类似于资源分配和回收的功能。为此，我们设计了补充方案2，其采用预先分配方式，支持上述功能：由manager角色从可用的资源中提前分配一定数量的资源给业务人员，即在表resource中从member\_id列为0的行中选择一定数量的行并将member\_id列设置为给定的member\_id；在各个数据中心中应用根据业务人员的member\_id从表resource中选择已经分配给此业务人员的资源，关联或去关联特定表，从而实现了操作数据集的不重合。


总而言之，要根据实际情况和业务需求设计方案。没有最好的方案，只有最适合的方案。





\section{附录—64位ID生成代码}

\begin{lstlisting}[framerule=0pt,escapeinside=``]
package qch.concurrent;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Qu Chunhe on 2020-06-15.
 */
public class Id {

    public Id() {
        this(22);
    }

    public Id(int bits) {
        BITS = (bits>31) || (bits<1) ? 22 : bits;
        ID_BOUND = (1 << BITS) - 1;
        SERVICE_ID_MASK = (1 << (32 - BITS)) - 1;
        autoIncrementId = new AtomicInteger(ThreadLocalRandom.current().nextInt(ID_BOUND));
    }

    public BigInteger next(long unixTime, int serviceId) {
        BigInteger highPart = new BigInteger(Long.toUnsignedString(unixTime));
        highPart = highPart.shiftLeft(32);
        long lowPart = ((serviceId & SERVICE_ID_MASK) << BITS) | next();

        return highPart.or(new BigInteger(Long.toUnsignedString(lowPart)));
    }

    private int next() {
        int currentId;
        do {
            currentId = autoIncrementId.getAndIncrement();
            if (currentId > ID_BOUND) {
                synchronized (lock) {
                    if (autoIncrementId.get() > ID_BOUND) {
                        autoIncrementId.set(0);
                    }
                }
            }
        } while (currentId > ID_BOUND);

        return currentId;
    }

    private final int BITS;
    private final int ID_BOUND;
    private final long SERVICE_ID_MASK;

    private final AtomicInteger autoIncrementId;
    private final Object lock = new Object();
}
\end{lstlisting}

# 引用
[1] AUTO_INCREMENT Handling in InnoDB, https://dev.mysql.com/doc/refman/5.7/en/innodb-auto-increment-handling.html


[2] Spider Server System Variables, https://mariadb.com/kb/ko/spider-server-system-variables/


@online{spider2,
author = {Spider\_Overview},
title = {Spider Storage Engine Overview},
howpublished = "\url{https://mariadb.com/kb/en/spider-storage-engine-overview/}"
}

@online{partition,
author = {Partition},
title = {Partitioning Overview},
howpublished = "\url{https://mariadb.com/kb/en/partitioning-overview/}"
}

@online{binlog2,
author = {Binlog\_Connector},
title = {MySQL Binlog Connector Java},
howpublished = "\url{https://github.com/shyiko/mysql-binlog-connector-java}"
}



@online{autoincrement,
author = {auto\_increment},
title = {Auto Increment Variables},
howpublished = "\url{https://dev.mysql.com/doc/refman/5.7/en/replication-options-master.html#sysvar_auto_increment_increment}"
}

@online{kafka,
author = {Producer\_Configst},
title = {Kafka Producer Configs},
howpublished = "\url{http://kafka.apache.org/documentation/#producerconfigs}"
}
