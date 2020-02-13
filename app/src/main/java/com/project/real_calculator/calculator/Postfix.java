package com.project.real_calculator.calculator;

/*
 * Postfix p = new Postfix();
 * p.eval("postfix"); = a double number
 */
public class Postfix
{
    Stack<Double> stack;

    public Postfix()
    {
        stack = new Stack<Double>();
    }

    public double eval(String expr)
    {
        String[] arr = expr.split(" ");

        for(String s : arr)
        {
            switch(s)
            {
                case "+":
                {
                    double a = stack.pop();
                    double b = stack.pop();
                    double c = a + b;
                    stack.push(c);
                }
                break;
                case"-":
                {
                    double a = stack.pop();
                    double b = stack.pop();
                    double c = b - a;
                    stack.push(c);
                }
                break;
                case"*":
                {
                    double a = stack.pop();
                    double b = stack.pop();
                    double c = a * b;
                    stack.push(c);
                }
                break;
                case"/":
                {
                    double a = stack.pop();
                    double b = stack.pop();
                    double c = b / a;
                    stack.push(c);
                }
                break;
                case"^":
                {
                    double a = stack.pop();
                    double b = stack.pop();
                    double c = Math.pow(b,a);
                    stack.push(c);
                }
                break;
                case"pi":
                {
                    double a = Math.PI;
                    stack.push(a);
                }
                break;
                default:
                {
                    if(s.equals(""))
                    {
                        continue;
                    }
                    else stack.push(Double.parseDouble(s));
                }

            }
        }
            return stack.peek();

    }

}