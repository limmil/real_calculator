package com.limmil.real_calculator.calculator;

/*
 * SignChange a = new SignChange();
 * a.sChan("--3---4)
 */
public class SignChange
{
    String output;

    public SignChange()
    {
        output = "";
    }

    public String change(String infix)
    {
        String[] temp = infix.split("");
        String[] arr = new String[temp.length];
        Stack<String> stack = new Stack<String>();
        String s = "";
        int c = 0;
        for(int i=0; i<temp.length; i++)
        {
            if(temp[i].equals("-") || temp[i].equals("+") )
            {
                if(stack.empty())
                {
                    stack.push(temp[i]);
                }
                else
                {
                    if(temp[i].equals(stack.peek()))
                    {
                        stack.pop();
                        stack.push("+");
                    }
                    else
                    {
                        stack.pop();
                        stack.push("-");
                    }
                }
            }
            else if( isNumeric(temp[i]) || isOp(temp[i]) )
            {
                if(!stack.empty())
                {
                    arr[c] = stack.pop();
                    arr[c+1] = temp[i];
                    c += 2;
                }
                else
                {
                    arr[c] = temp[i];
                    c++;
                }
            }
            else continue;
        }

        for(int i=0; i<arr.length; i++)
        {
            if(arr[i]!=null) s += arr[i];
        }
        output = s;

        return output;
    }

    private boolean isNumeric(String str)
    {
        if(str.equals(".") || str.equals("p") || str.equals("i"))
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
            case"(": case")":
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

    public static boolean check(String s)
    {
        if(s.equals("") || s.equals("error")) return false;
        SignChange a = new SignChange();
        s = a.change(s);
        String[] arr = s.split("");
        int j = 0;
        int k = 0;
        for(int i=0; i<arr.length; i++)
        {
            switch(arr[i])
            {
                case"(":
                {
                    if(arr.length >= 2 && arr[i+1].equals(")")) return false;
                    if(arr.length==1) return false;
                    int o = i;
                    boolean triger = true;
                    while(o<=arr.length && triger)
                    {
                        if(arr[o].equals(")"))
                        {
                            triger = false;
                            j++;
                        }
                        o++;
                    }
                }
                break;
                case")":
                {
                    if(arr.length==1) return false;
                    int o = i;
                    boolean triger = true;
                    while(o>=0 && triger)
                    {
                        if(arr[o].equals("("))
                        {
                            triger = false;
                            k++;
                        }
                        o--;
                    }
                }
                break;
                case"*": case"/": case"^":
                if(i == 0) return false;
                else if(i == arr.length) return false;
                if(i>=1)
                {
                    switch(arr[i-1])
                    {
                        case"+": case"-": case"(":
                        case"*": case"/":
                        return false;
                    }
                }
                break;

            }
        }

        if(j!=k) return false;

        return true;
    }
}