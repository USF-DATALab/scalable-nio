# scalable-nio

C10k
* https://en.wikipedia.org/wiki/C10k_problem
* http://www.kegel.com/c10k.html

NIO
* https://en.wikipedia.org/wiki/New_I/O_(Java)
* http://tutorials.jenkov.com/java-nio/index.html

General Idea: a single server thread uses the Selector to multiplex incoming connections. Once a job/task/message has been received by the selector, then it is put in a work queue for worker threads to consume.

Send message + checksum -> verify on the server side.
