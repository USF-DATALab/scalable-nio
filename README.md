# scalable-nio

C10k
* https://en.wikipedia.org/wiki/C10k_problem
* http://www.kegel.com/c10k.html

NIO
* https://en.wikipedia.org/wiki/New_I/O_(Java)
* http://tutorials.jenkov.com/java-nio/index.html

General Idea: a single server thread uses the Selector to multiplex incoming connections. Once a job/task/message has been received by the selector, then it is put in a work queue for worker threads to consume.

Send message + checksum -> verify on the server side.


## Benchmarks / Test Cases
* Sending random data + checksum, verify checksum on the server side
* Sending large messages: we should be able to max out the interface speed (~120 MB/s or so on gigabit)
* Allowing a large number of client connections: at least 10k if not 100k. We can create clients that open many connections and spread them across department machines (kudlick, g12, etc)
* Adding sleeps to the logic to force buffers to fill up: for example, if the server sleeps for 1 second upon receiving a message, this will cause a backlog at the client.
