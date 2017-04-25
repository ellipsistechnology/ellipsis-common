package ellipsis.common;

import java.util.concurrent.ArrayBlockingQueue;

public class ThreadPool 
{
	private static final int SLEEP_TIME = 10;
	private static final int TASK_QUEUE_CAPACITY = 25*1024*1024;
	
	//// Static Instance ////
	
	private static ThreadPool instance;
	public static int initialThreadCount = 16;
	
	public static ThreadPool getInstance()
	{
		if(instance == null)
			instance = new ThreadPool(initialThreadCount);
		return instance;
	}
	
	
	//// Pool Implementation ////
	
	private ArrayBlockingQueue<Runnable> tasks;
	private int taskCount = 0;
	private ThreadLocal<Boolean> disallowed = new ThreadLocal<Boolean>();
	
	public ThreadPool(int initialThreadCount)
	{
		tasks = new ArrayBlockingQueue<Runnable>(TASK_QUEUE_CAPACITY);
		
		for (int i = 0; i < initialThreadCount; i++) 
		{
			new Thread()
			{
				@Override
				public void run() 
				{
					disallowed.set(false);
					
					while(true)
					{
						Runnable task;
						synchronized (tasks)
						{
							task = tasks.poll();
						}
						if(task != null)
						{
							task.run();
							synchronized (tasks)
							{
								taskCount--;
							}
						}
						else
							try {
								Thread.sleep(SLEEP_TIME);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
					}
				}	
			}.start();
		}
	}
	
	/**
	 * If the calling thread has set disallowed then the given task
	 * will be executed immediately, otherwise it will be queued
	 * for execution on the next available thread.
	 * @param task
	 */
	public void queueTask(Runnable task)
	{
		Boolean dis = disallowed.get();
		if(dis != null && dis)
		{
			task.run();
		}
		else
		{
			synchronized(tasks)
			{
				++taskCount;
				tasks.add(task);
			}
		}
	}
	
	/**
	 * Specifies if future calls to queueTask from this thread should be allowed to queue.
	 * If set to false, future calls to queueTask will execute the task immediately on the same thread.
	 * @param disallow
	 */
	public void setDisallowed(boolean disallow)
	{
		disallowed.set(disallow);
	}
	
	public void waitForAll()
	{
		while(true)
		{
			synchronized (tasks) 
			{
				if(taskCount == 0)
					return;
			}
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//// TEST ////
	
	public static void main(String[] args) 
	{
		initialThreadCount = 2;
		
		final int SUM_TO1 = (int)30e6;
		final int SUM_TO2 = (int)60e6;
		final int SUM_TO3 = (int)10e6;
		
		final Runnable r1 = new Runnable() 
		{
			@Override
			public void run() 
			{
				System.out.println("r1 started");
				for(int i = 0; i < SUM_TO1; ++i)
				{
					Math.pow(1275.08632, 2232.1466);
					if(i%1e6 == 0)
						System.out.println("r1: "+(int)((100.0*i)/SUM_TO1)+"%");
				}
				System.out.println("r1 completed");
			}
		};
		
		final Runnable r2 = new Runnable() 
		{
			@Override
			public void run() 
			{
				System.out.println("r2 started");
				for(int i = 0; i < SUM_TO2; ++i)
				{
					Math.pow(1275.08632, 2232.1466);
					if(i%1e6 == 0)
						System.out.println("\tr2: "+(int)((100.0*i)/SUM_TO2)+"%");
				}
				System.out.println("r2 completed");
			}
		};
		
		Runnable r3 = new Runnable() 
		{
			@Override
			public void run() 
			{
				System.out.println("r3 started");
				for(int i = 0; i < SUM_TO3; ++i)
				{
					Math.pow(1275.08632, 2232.1466);
					if(i%1e6 == 0)
						System.out.println("\t\tr3: "+(int)((100.0*i)/SUM_TO3)+"%");
				}
				System.out.println("r3 completed");
			}
		};
		
		ThreadPool.getInstance().queueTask(r1);
		ThreadPool.getInstance().queueTask(r2);
		ThreadPool.getInstance().queueTask(r3);
		
		ThreadPool.getInstance().waitForAll();
		
		System.out.println("Test 2:");
		
		ThreadPool.getInstance().queueTask(new Runnable() 
		{
			@Override
			public void run() {
				System.out.println("r4 started");

				ThreadPool.getInstance().setDisallowed(true);
				
				ThreadPool.getInstance().queueTask(r1);

				ThreadPool.getInstance().setDisallowed(false);
				
				ThreadPool.getInstance().queueTask(r2);
				
				for(int i = 0; i < SUM_TO3; ++i)
				{
					Math.pow(1275.08632, 2232.1466);
					if(i%1e6 == 0)
						System.out.println("\t\t\tr4: "+(int)((100.0*i)/SUM_TO3)+"%");
				}
				System.out.println("r4 completed");
			}
		});
		
		ThreadPool.getInstance().waitForAll();
		
		System.out.println("All complete.");
		
		System.exit(0);
	}
}
