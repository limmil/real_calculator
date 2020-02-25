package com.project.real_calculator.calculator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Calculate
{
    public static String compute(String disp)
    {
        String str = "";
        try {
            ShuntingYard sy = new ShuntingYard();
            Postfix pf = new Postfix();
            str = sy.convert(disp);


            if (str.equals("error"))
            {
                return str;
            }
            else
            {
                String answer = ( "" + pf.eval(str));
                if (answer.length() > 9)
                {
                    answer = scientificOutput(answer);
                }
                if(answer.indexOf('E') != -1)
                {
                    answer = answer.replace("E", "E(");
                    answer = answer + ")";
                }

                return answer;
            }
        }
        catch (Exception e)
        {
            return "error";
        }
    }

    private static String scientificOutput(String input)
    {
        DecimalFormat formatter = new DecimalFormat("#.#######E0");
        Double d = Double.parseDouble(input);
        return formatter.format(d);
    }
}
