package lych.soullery.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class DequeNBTOperator<T extends INBT> {
    private final String listName;
    private final int type;
    private final int size;

    public DequeNBTOperator(String listName, int size, int type) {
        this.listName = listName;
        this.type = type;
        this.size = size;
    }

    public void construct(CompoundNBT compoundNBT) {
        syncData(compoundNBT, new ListNBT());
    }

    public void push(CompoundNBT compoundNBT, T element) {
        ListNBT listNBT = asList(compoundNBT);
        checkSize(listNBT);
        listNBT.add(element);
        syncData(compoundNBT, listNBT);
    }

    private void syncData(CompoundNBT compoundNBT, ListNBT listNBT) {
        compoundNBT.put(listName, listNBT);
    }

    private void checkSize(ListNBT listNBT) {
        if (listNBT.size() >= size) {
            throw new UnsupportedOperationException("Deque is full");
        }
    }

    public T pop(CompoundNBT compoundNBT) {
        ListNBT listNBT = asList(compoundNBT);
        checkNotEmpty(listNBT);
        T t = (T) listNBT.remove(listNBT.size() - 1);
        syncData(compoundNBT, listNBT);
        return t;
    }

    private static void checkNotEmpty(ListNBT listNBT) {
        if (listNBT.isEmpty()) {
            throw new NoSuchElementException("No element present");
        }
    }

    public void addFirst(CompoundNBT compoundNBT, T element) {
        ListNBT listNBT = asList(compoundNBT);
        checkSize(listNBT);
        listNBT.add(0, element);
        syncData(compoundNBT, listNBT);
    }

    public T removeFirst(CompoundNBT compoundNBT) {
        ListNBT listNBT = asList(compoundNBT);
        checkNotEmpty(listNBT);
        T t = (T) listNBT.remove(0);
        syncData(compoundNBT, listNBT);
        return t;
    }

    public T top(CompoundNBT compoundNBT) {
        ListNBT listNBT = asList(compoundNBT);
        return (T) listNBT.get(listNBT.size() - 1);
    }

    public T front(CompoundNBT compoundNBT) {
        return (T) asList(compoundNBT).get(0);
    }

    public ListNBT asList(CompoundNBT compoundNBT) {
        if (!compoundNBT.contains(listName, Constants.NBT.TAG_LIST)) {
            construct(compoundNBT);
        }
        return compoundNBT.getList(listName, type);
    }

    public int size(CompoundNBT compoundNBT) {
        return asList(compoundNBT).size();
    }

    public int maxSize() {
        return size;
    }

    public boolean isEmpty(CompoundNBT compoundNBT) {
        return asList(compoundNBT).isEmpty();
    }

    public boolean isFull(CompoundNBT compoundNBT) {
        return size(compoundNBT) >= size;
    }
}
