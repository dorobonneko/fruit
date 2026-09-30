package moe.fruit.util;
import java.util.concurrent.*;

public class Async {
	public static <E extends Object> Then<E> async(Supplier<E> exe, Executor exec) {
		Then<E> then = new Then(exe, exec);
		return then;
	}
	public static class Then<T> {
		private Executor asynce;
		private Supplier<T> async;
		Then(Supplier<T> async, Executor asynce) {
			this.async = async;
			this.asynce = asynce;
		}
		public void then(Consumer<T> consumer, Executor exec) {
			then(consumer, null, exec);
		}
		public void then(Consumer<T> consumer, Consumer<Exception> err, Executor exec) {
			/*this.consumer = consumer;
			this.exec = exec;
			this.error = (Async.Consumer<Exception>) err;*/
			asynce.execute(new Runnable() {

				@Override
				public void run() {
					try {
						T result = async.get();
						if (exec != null)
							exec.execute(() -> consumer.accept(result));
						else
							consumer.accept(result);
					} catch (Exception e) {
						if (err != null) {
							if (exec != null)
								exec.execute(() -> {
									err.accept(e);
								});
							else
								err.accept(e);
						} else
							throw e;
					}
				}
			});
		}
		
	}
	public interface Supplier<T> {
		T get();
	}
	public interface Consumer<T> {
		void accept(T t);
	}
}

