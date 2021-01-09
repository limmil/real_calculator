package com.limmil.real_calculator.calculator;
import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * Postfix p = new Postfix();
 * p.eval("postfix"); = a BigDecimal number
 */
public class Postfix
{
    Stack<BigDecimal> stack;

    public Postfix()
    {
        stack = new Stack<BigDecimal>();
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
                    BigDecimal a = stack.pop();
                    BigDecimal b = stack.pop();
                    BigDecimal c = a.add(b);
                    stack.push(c);
                }
                break;
                case"-":
                {
                    BigDecimal a = stack.pop();
                    BigDecimal b = stack.pop();
                    BigDecimal c = b.subtract(a);
                    stack.push(c);
                }
                break;
                case"*":
                {
                    BigDecimal a = stack.pop();
                    BigDecimal b = stack.pop();
                    BigDecimal c = a.multiply(b);
                    stack.push(c);
                }
                break;
                case"/":
                {
                    BigDecimal a = stack.pop();
                    BigDecimal b = stack.pop();
                    BigDecimal c = b.divide(a,7, RoundingMode.DOWN);
                    stack.push(c);
                }
                break;
                case"^":
                {
                    BigDecimal a = stack.pop();
                    BigDecimal b = stack.pop();
                    BigDecimal c = new BigDecimal("" + Math.pow(b.doubleValue(), a.doubleValue()));
                    stack.push(c);
                }
                break;
                case"pi":
                {
                    BigDecimal a = new BigDecimal("" + Math.PI);
                    stack.push(a);
                }
                break;
                default:
                {
                    if(s.equals(""))
                    {
                        continue;
                    }
                    else stack.push(new BigDecimal(s));
                }

            }
        }
            return stack.peek().doubleValue();
    }

}