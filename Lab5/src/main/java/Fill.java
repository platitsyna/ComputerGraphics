import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;


class Table<E> {
    List<List<E>> list = new ArrayList<List<E>>();

    public void addLine(List<E> line) {
        list.add(line);
    }

    public E get(int y, int x) {
        return list.get(y).get(x);
    }

    public void set(int y, int x, E value) {
        list.get(y).set(x, value);
    }

    public int cols(int row) {
        return list.get(row).size();
    }

    public int rows() {
        return list.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (List<E> row : list) {
            for (E el : row) {
                sb.append(el).append(' ');
            }
            sb.append('\n');
        }

        return sb.toString();
    }
}

public class Fill {
    public static void main(String args[]) throws FileNotFoundException {
        Table<Character> table = new Table<Character>();
        File f = new File("fill.txt");

        Scanner scan = new Scanner(f);
        String line;
        List<Character> charLine;

        while (scan.hasNextLine()) {
            line = scan.nextLine();

            charLine = new ArrayList<Character>();

            for (int i = 0; i < line.length(); i++) {
                charLine.add(line.charAt(i));
            }

            table.addLine(charLine);
        }

        System.out.println(table);

        fill(table, '1', 'x');

        System.out.println(table);


    }

    public static int fill(Table<Character> table, Character toFind, Character toReplace) {
        // счетчик найденных фигур
        int count = 0;

        // внутренний класс для хранения координат
        class Coord {
            int y;
            int x;

            public Coord(int y, int x) {
                this.y = y;
                this.x = x;
            }
        }

        Stack<Coord> stack = new Stack<Coord>();

        int[] nextRows = {-1, 1};

        // цикл по всем точкам
        for (int i = 0; i < table.rows(); i++) {
            for (int j = 0; j < table.cols(i); j++) {
                // если точка - искомая, то в этой фигуре запускаем заливку
                if (table.get(i, j).equals(toFind)) {
                    // увеличиваем счетчик для найденных фигур
                    count++;

                    // запихиваем координаты точки в стек
                    stack.push(new Coord(i, j));
                    while (!stack.empty()) {
                        // извлекаем координаты из стека
                        Coord cur = stack.pop();

                        // если извлеченная точка еще не закрашена - закрашиваем
                        if (table.get(cur.y, cur.x).equals(toFind)) {
                            table.set(cur.y, cur.x, toReplace);
                        }

                        // закрашиваем все точки слева от текущей
                        int xl = cur.x - 1;

                        while (xl >= 0 && table.get(cur.y, xl).equals(toFind)) {
                            table.set(cur.y, xl, toReplace);
                            xl--;
                        }

                        // закрашиваем все точки справа от текущей
                        int xr = cur.x + 1;

                        while (xr < table.cols(cur.y) && table.get(cur.y, xr).equals(toFind)) {
                            table.set(cur.y, xr, toReplace);
                            xr++;
                        }

                        // теперь переходим к строке снизу и сверху
                        for (int k = 0; k < nextRows.length; k++) {
                            int dy = nextRows[k];

                            // проверяем, чтобы не вышло за пределы
                            if (dy < 0 || dy >= table.rows()) {
                                continue;
                            }

                            // берем левое x и ищем начало строки для заливки
                            int x = xl + 1;

                            // пока x не достиг правого края только что залитой строки
                            while (x < xr) {
                                int x0 = x;

                                // ищем то, что нужно заливать
                                while (x < xr && table.get(dy, x).equals(toFind)) {
                                    x++;
                                }

                                // если не равно - значит хоть одна итерация была - добавляем точку в стек
                                if (x0 != x) {
                                    stack.push(new Coord(dy, x));
                                }

                                // теперь пропускаем всё то, что заливать не надо - на случай "дырки"
                                while (x < xr && !table.get(dy, x).equals(toFind)) {
                                    x++;
                                }
                            }

                        }

                    }

                }


            }
        }

        return count;
    }
}