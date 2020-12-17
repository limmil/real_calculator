package com.limmil.real_calculator.calculator;

/*
 * ShuntingYard a = new ShuntingYard();
 * String i = a.convert("infix");
 * 3--3.14, 10+-3.14, -(-3+4),
 *
 */

public class ShuntingYard
{
    String output;

    public ShuntingYard()
    {
        output = "";
    }

    private int prec(String op)
    {
        switch(op)
        {
            case "+":
            case "-":
                return 0;
            case "*":
            case "/":
                return 1;
            case "^":
                return 2;
            default:
                return -1;
        }
    }

    public String convert(String infix)
    {

        if(SignChange.check(infix))
        {
            SignChange q = new SignChange();
            String[] arr = q.change(infix).split("");
            Stack<String> stack = new Stack<String>();
            String temp = "";

            for(int i=0; i<arr.length; i++)
            {
                if(isNumeric(arr[i]))
                {
                    temp += arr[i];
                }
                else if( temp.equals("") && (arr[i].equals("-") || arr[i].equals("+")) && arr[i+1].equals("(") )
                {
                    temp += arr[i] + "1" + " ";
                    stack.push("*");
                }
                else if(temp.equals("") && (arr[i].equals("-") || arr[i].equals("+")) )
                {
                    temp += arr[i];
                }
                else if((arr[i].equals("-") || arr[i].equals("+")) && arr[i-1].equals("(") )
                {
                    temp += arr[i];
                }
                else if(isOp(arr[i]))
                {
                    temp += " ";
                    if (stack.empty())
                    {
                        stack.push(arr[i]);
                    }
                    else
                    {
                        if(prec(arr[i]) > prec(stack.peek()))
                        {
                            stack.push(arr[i]);
                        }
                        else if(prec(arr[i]) <= prec(stack.peek()))
                        {
                            if(arr[i].equals("^") && stack.peek().equals("^"))
                            {
                                stack.push(arr[i]);
                            }
                            else
                            {
                                while(!stack.empty() && stack.peek().equals("^"))
                                {
                                    temp += stack.pop() + " ";
                                }
                                if(!stack.empty()) temp += stack.pop() + " ";
                                stack.push(arr[i]);
                            }
                        }
                    }
                }
                else if(arr[i].equals("p") && arr[i+1].equals("i"))
                {
                    if(i>=1 && isNumeric(arr[i-1]))
                    {
                        temp += " " + arr[i] + arr[i+1];
                        stack.push("*");
                    }
                    else temp += arr[i] + arr[i+1];
                }
                else if(arr[i].equals("("))
                {
                    if(i>=1)
                    {
                        if(isNumeric(arr[i-1]) || arr[i-1].equals(")"))
                        {
                            stack.push("*");
                            temp += " ";
                        }
                    }
                    stack.push(arr[i]);
                }
                else if(arr[i].equals(")"))
                {
                    if(!stack.peek().equals("(")) temp += " " + stack.pop();

                    if(!stack.empty())
                    {
                        while(!stack.peek().equals("("))
                        {
                            temp += " " + stack.pop();
                        }
                        stack.pop();
                    }
                }
                else continue;
            }

            if(!stack.empty())
            {
                temp += " " + stack.pop() + " ";
                while(!stack.empty())
                {
                    temp += stack.pop() + " ";
                }
            }

            output = temp;

            return output;
        }
        else
        {
            output = "error";
            return output;
        }
    }

    private boolean isNumeric(String str)
    {
        if(str.equals("."))
        {
            return true;
        }

        try{
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    private boolean isOp(String s)
    {
        switch(s)
        {
            case"+":
            case"-":
            case"*":
            case"/":
            case"^":
                return true;
            default:
                return false;
        }
    }
}
