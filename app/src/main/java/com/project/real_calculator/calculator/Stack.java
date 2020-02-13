package com.project.real_calculator.calculator;

public class Stack<X>
{
    private LList<X> stack;

    public Stack()
    {
        stack = new LList<X>();
    }

    public void push(X value)
    {
        stack.addFirst(value);
    }

    public X pop()
    {
        X temp = peek();
        stack.removeFirst();
        return temp;
    }

    public X peek()
    {
        return stack.get(0);
    }

    public int size()
    {
        return stack.size();
    }

    public boolean empty()
    {
        return size() == 0;
    }

    public void clear()
    {
        stack.clear();
    }
}
