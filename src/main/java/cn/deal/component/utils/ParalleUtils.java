package cn.deal.component.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 并发工具
 */
public class ParalleUtils {

	public static <T> void asyncForEach(List<T> list, Consumer<? super T> action) throws RuntimeException {
		if (list!=null && list.size()>0) {
			List<RuntimeException> errors = Collections.synchronizedList(new ArrayList<RuntimeException>());
			
			list.parallelStream().forEach(it->{
				try {
					action.accept(it);
				} catch(RuntimeException e) {
					errors.add(e);
				}
			});
			
			if (errors!=null && errors.size()>0) {
				throw errors.get(0);
			}
		}
	}
	
	public static <T> void forEach(List<T> list, Consumer<? super T> action) throws RuntimeException {
		if (list!=null && list.size()>0) {
			List<RuntimeException> errors = Collections.synchronizedList(new ArrayList<RuntimeException>());
			
			list.stream().forEach(it->{
				try {
					action.accept(it);
				} catch(RuntimeException e) {
					errors.add(e);
				}
			});
			
			if (errors!=null && errors.size()>0) {
				throw errors.get(0);
			}
		}
	}
}
