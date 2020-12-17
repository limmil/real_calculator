package com.limmil.real_calculator.calculator;

public class LList<Z>
{
    private Node<Z> head;
    private Node<Z> last;

    public LList()
    {
        head = null;
        last = null;
    }

    public void add(Z v)
    {
        addLast(v);
    }

    public int size()
    {
        Node<Z> p = head;
        int count = 0;
        while(p != null)
        {
            p = p.next;
            count ++;
        }

        return count;
    }

    public String toString()
    {
        String s;
        int count = 0;
        Node p = head;
        if (head == null)
        {
            return "[]";
        }
        else
        {
            s = "[";
            while ( count != size()-1 )
            {
                s += p.value + ", ";
                p = p.next;
                count++;
            }
            s += p.value + "]";
        }

        return s;
    }

    public Z get(int index)
    {
        if(index < 0 || index >= size())
        {
            throw new RuntimeException();
        }
        else
        {
            Node<Z> p = head;
            int count = 0;
            while (count != index)
            {
                p = p.next;
                count++;
            }
            return p.value;
        }
    }

    public void remove()
    {
        removeLast();
    }

    public void removeFirst()
    {
        int count = 0;
        if(head == null)
        {
            throw new RuntimeException();
        }
        else
        {
            head = head.next;
        }
    }

    public void removeLast()
    {
        if(head == null)
        {
            throw new RuntimeException();
        }
        else if(head == last)
        {
            removeFirst();
        }
        else
        {
            last = last.prev;
            last.next = null;
        }
    }

    public void insert(int index, Z v)
    {
        if(index < 0 || index > size())
        {
            throw new RuntimeException();
        }
        else if(index == 0)
        {
            addFirst(v);
        }
        else
        {
            Node<Z> p = head;
            int count = 0;
            while (count != index)
            {
                p = p.next;
                count++;
            }
            Node<Z> a = new Node<Z>();
            a.value = v;
            a.next = p;
            a.prev = p.prev;
            p.prev.next = a;
            p.prev = a;
        }

    }

    public void addLast(Z v)
    {
        if(head == null)
        {
            addFirst(v);
        }
        else
        {
            Node<Z> a = new Node<Z>();
            a.value = v;
            a.prev = last;
            a.next = null;
            last.next = a;
            last = a;
        }
    }

    public void addFirst(Z v)
    {
        if(head == null)
        {
            Node<Z> a = new Node<Z>();
            a.value = v;
            a.next = head;
            a.prev = null;
            last = a;
            head = a;
        }
        else
        {
            Node<Z> a = new Node<Z>();
            a.value = v;
            a.next = head;
            head.prev = a;
            a.prev = null;
            head = a;
        }
    }

    public void set(int index, Z v)
    {
        if(head == null || index >= size())
        {
            throw new RuntimeException();
        }
        else
        {
            Node<Z> p = head;
            int count = 0;
            while (count != index)
            {
                p = p.next;
                count++;
            }
            p.value = v;
        }
    }

    public void delete(int index)
    {
        if(head == null || index >= size())
        {
            throw new RuntimeException();
        }
        else if(index == 0)
        {
            removeFirst();
        }
        else if(index == size()-1)
        {
            removeLast();
        }
        else
        {
            Node<Z> p = head;
            int count = 0;
            while (count != index)
            {
                p = p.next;
                count++;
            }
            p.prev.next = p.next;
            p.next.prev = p.prev;
        }
    }

    public int indexOf(Z target)
    {
        Node<Z> p = head;
        int count = 0;

        while(count != size())
        {
            if (p.value.equals(target))
            {
                return count;
            }
            p = p.next;
            count++;
        }
        return -1;
    }

    public boolean contains(Z target)
    {
        return (indexOf(target) > -1);
    }

    public void clear()
    {
        head = null;
        last = null;
    }
}
