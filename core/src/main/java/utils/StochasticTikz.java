package utils;

import structures.StochasticVariable;
import structures.arrays.ArrayOfLong;

public class StochasticTikz {

    private static int figurePerRow = 1;
    private static int figures = 0;
    private static double scale = 1;
    private static double xOffset = 0, yOffset = 0;

    public static void setFigurePerRow(int n){
        figurePerRow = n;
        scale = 1.0 / n;
    }

    public static void setOffsets(double x, double y) {
        xOffset = x;
        yOffset = y;
    }

    public static String convert(StochasticVariable[] X, double x){
        return convert(X, null, x);
    }

    public static String convert(StochasticVariable[] X, ArrayOfLong p, double x){
        return convert(X, p, x, x*1.6, x*.75);
    }

    public static String convert(StochasticVariable[] X, ArrayOfLong p, double x, double y, double space){
        double pow = Math.pow(10, X[0].getPrecision());
        StringBuilder builder = new StringBuilder();
        if(figures == 0) builder.append(String.format("\\scalebox{%s}{\n\\begin{tikzpicture}\n", scale));
        for(int i = 0; i < X.length; i++){
            builder.append("% X"); builder.append(i); builder.append("\n");
            if(p != null && p.get(i) > 0) wine(p.get(i)/pow, x, y, xOffset*figures+(x+space)*i, yOffset*figures, p.get(i) >= X[i].getMaxQuantity(), builder);
            glass(i, x, y, xOffset*figures+(x+space)*i, yOffset*figures, builder);
            bounds(X[i].getMaxQuantity()/pow, x, y, xOffset*figures+(x+space)*i, yOffset*figures, true, builder);
            if(X[i].getMinQuantity() > 0 && X[i].getMinQuantity() < X[i].getMaxQuantity()) bounds(X[i].getMinQuantity()/pow, x, y, xOffset*figures+(x+space)*i, yOffset*figures, false, builder);
            cost(X[i].getMaxValue()/pow, x, xOffset*figures+(x+space)*i, yOffset*figures, builder);
            builder.append("\n");
        }
        figures++;
        if(figures == figurePerRow) {
            builder.append("\\end{tikzpicture}\n}");
            figures = 0;
        }
        return builder.toString();
    }

    private static void wine(double value, double x, double y, double xOffset, double yOffset, boolean full, StringBuilder builder){
        builder.append("\\fill[violet!40!white] (");
        builder.append(xOffset);
        builder.append(", ");
        builder.append(yOffset);
        builder.append(") rectangle (");
        builder.append(x+xOffset);
        builder.append(", ");
        builder.append(value*y+yOffset);
        builder.append(");\n");

        if(full) return;
        builder.append("\\draw (1pt + ");
        builder.append(xOffset+x);
        builder.append("cm, ");
        builder.append(yOffset+value*y);
        builder.append("cm) -- (-1pt + ");
        builder.append(xOffset+x);
        builder.append("cm, ");
        builder.append(yOffset+value*y);
        builder.append("cm) node[anchor=west] {$");
        builder.append(value);
        builder.append("$};\n");
    }

    private static void glass(int i, double x, double y, double xOffset, double yOffset, StringBuilder builder){
        builder.append("\\draw (");
        builder.append(xOffset);
        builder.append(", ");
        builder.append(yOffset);
        builder.append(") rectangle (");
        builder.append(x+xOffset);
        builder.append(", ");
        builder.append(y);
        builder.append(");\n");

        builder.append("\\draw (");
        builder.append(xOffset+x/2);
        builder.append(", ");
        builder.append(yOffset+y);
        builder.append(") node[anchor=south] {$X_");
        builder.append(i);
        builder.append("$};\n");
    }

    private static void bounds(double bound, double x, double y, double xOffset, double yOffset, boolean upper, StringBuilder builder){
        if(upper) builder.append("\\draw[blue,thick,dashed] (");
        else builder.append("\\draw[red,thick] (");
        builder.append(xOffset);
        builder.append(", ");
        builder.append(yOffset+bound*y);
        builder.append(") -- (");
        builder.append(xOffset+x);
        builder.append(", ");
        builder.append(yOffset+bound*y);
        builder.append(");\n");
        builder.append("\\draw (1pt + ");
        builder.append(xOffset);
        builder.append("cm, ");
        builder.append(yOffset+bound*y);
        builder.append("cm) -- (-1pt + ");
        builder.append(xOffset);
        builder.append("cm, ");
        builder.append(yOffset+bound*y);
        builder.append("cm) node[anchor=east] {$");
        if(bound > 0) builder.append(bound);
        else builder.append(0);
        builder.append("$};\n");
    }

    private static void cost(double cost, double x, double xOffset, double yOffset, StringBuilder builder){
        builder.append("\\draw (");
        builder.append(xOffset+x/2);
        builder.append(", ");
        builder.append(yOffset);
        builder.append(") node[anchor=north] {$");
        builder.append(cost);
        builder.append("$};\n");
    }

}
