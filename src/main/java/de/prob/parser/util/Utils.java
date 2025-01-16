package de.prob.parser.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {

	public static <T> List<T> sortByTopologicalOrder(final Map<T, Set<T>> dependencies) {
		final Set<T> allValues = new HashSet<>(dependencies.keySet());
		ArrayList<T> sortedList = new ArrayList<>();
		boolean newRun = true;
		while (newRun) {
			newRun = false;
			final ArrayList<T> todo = new ArrayList<>(allValues);
			todo.removeAll(sortedList);
			for (T element : todo) {
				Set<T> deps = new HashSet<>(dependencies.get(element));
				deps.removeAll(sortedList);
				if (deps.isEmpty()) {
					sortedList.add(0,element);
					newRun = true;
				}
			}
		}
		return sortedList;
	}
}
