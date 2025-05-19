package game.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeferredList<T> implements Iterable<T> {

  private final List<T> mainList;
  private final List<T> toAdd;
  private final List<T> toRemove;

  public DeferredList(List<T> mainList, List<T> toAdd, List<T> toRemove) {
    this.mainList = mainList;
    this.toAdd = toAdd;
    this.toRemove = toRemove;
  }

  public DeferredList() {
    this.mainList = new ArrayList<>();
    this.toAdd = new ArrayList<>();
    this.toRemove = new ArrayList<>();
  }

  public void add(T element) {
    // TODO: directly access mainList if not iterating?
    toAdd.add(element);
  }

  public void remove(T element) {
    // TODO: directly access mainList if not iterating?
    toAdd.remove(element); // not yet added
    toRemove.add(element);
  }

  public void applyPendingChanges() {
//    System.out.println("Applying pending changes" + toAdd.size() + "/" + toRemove.size());

    mainList.removeAll(toRemove);
    toRemove.clear();

    mainList.addAll(toAdd);
    toAdd.clear();
  }

  public int size() {
    return mainList.size();
  }

  @Override
  public Iterator<T> iterator() {
    return new DeferredListIterator(mainList.iterator());
  }

  public class DeferredListIterator implements Iterator<T> {

    private final Iterator<T> it;

    public DeferredListIterator(Iterator<T> it) {
      this.it = it;
    }

    @Override
    public boolean hasNext() {
      return it.hasNext();
    }

    @Override
    public T next() {
      return it.next();
    }
  }

}
