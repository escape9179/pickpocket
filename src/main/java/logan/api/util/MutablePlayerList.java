package logan.api.util;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MutablePlayerList implements List<Player> {

    private final ArrayList<Player> delegate = new ArrayList<>();

    public static MutablePlayerList of(Player... players) {
        MutablePlayerList list = new MutablePlayerList();
        for (Player player : players) {
            list.add(player);
        }
        return list;
    }

    @Override
    public boolean contains(Object element) {
        if (!(element instanceof Player player)) return false;
        for (Player p : delegate) {
            if (p.getUniqueId().equals(player.getUniqueId())) return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object element) {
        if (!(element instanceof Player player)) return false;
        return delegate.removeIf(p -> p.getUniqueId().equals(player.getUniqueId()));
    }

    // Delegate all remaining List methods to the ArrayList

    @Override public int size() { return delegate.size(); }
    @Override public boolean isEmpty() { return delegate.isEmpty(); }
    @Override public Iterator<Player> iterator() { return delegate.iterator(); }
    @Override public Object[] toArray() { return delegate.toArray(); }
    @Override public <T> T[] toArray(T[] a) { return delegate.toArray(a); }
    @Override public boolean add(Player player) { return delegate.add(player); }
    @Override public boolean containsAll(Collection<?> c) { return delegate.containsAll(c); }
    @Override public boolean addAll(Collection<? extends Player> c) { return delegate.addAll(c); }
    @Override public boolean addAll(int index, Collection<? extends Player> c) { return delegate.addAll(index, c); }
    @Override public boolean removeAll(Collection<?> c) { return delegate.removeAll(c); }
    @Override public boolean retainAll(Collection<?> c) { return delegate.retainAll(c); }
    @Override public void clear() { delegate.clear(); }
    @Override public Player get(int index) { return delegate.get(index); }
    @Override public Player set(int index, Player element) { return delegate.set(index, element); }
    @Override public void add(int index, Player element) { delegate.add(index, element); }
    @Override public Player remove(int index) { return delegate.remove(index); }
    @Override public int indexOf(Object o) { return delegate.indexOf(o); }
    @Override public int lastIndexOf(Object o) { return delegate.lastIndexOf(o); }
    @Override public ListIterator<Player> listIterator() { return delegate.listIterator(); }
    @Override public ListIterator<Player> listIterator(int index) { return delegate.listIterator(index); }
    @Override public List<Player> subList(int fromIndex, int toIndex) { return delegate.subList(fromIndex, toIndex); }
}
