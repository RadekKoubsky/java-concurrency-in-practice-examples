/**
 * Structuring applications around the executions of tasks can simplify
 * development and facilitate concurrency. The Executor framework permits
 * you to decouple task submission from execution policy and supports
 * a rich variety of execution policies; whenever you find yourself
 * creating threads to perform tasks, consider using an Executor instead.
 *
 * To maximize the benefit of decomposing an application into tasks, you must
 * identify sensible task boundaries. In some applications, the obvious
 * task boundaries work well, whereas in others some analysis may be
 * required to uncover fine-grained exploitable parallelism.
 */
package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution;