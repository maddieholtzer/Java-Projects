package db;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Scanner;
import java.util.StringJoiner;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;


public class Database {
    private int numberofTables;
    private HashMap<String, Table> listofTables;


    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+\\s+\\S+\\s*"
            + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                    + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                    + "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+"
                    + "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+"
                    + SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                    + "\\s*(?:,\\s*.+?\\s*)*)");

    public Database() {
        this.numberofTables = 0;
        this.listofTables = new HashMap<String, Table>(); //empty array map of tables.

    }

    public String transact(String query) {
        return eval(query);
    }


    private String eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            try {
                return select(m.group(1));
            } catch (RuntimeException e) {
                return "ERROR: No";
            }
        } else {
            return "ERROR: Malformed query";
        }
    }


    private String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            return "ERROR: Malformed insert";
        }
        System.out.printf("You are trying to insert the row \"%s\" into the table %s\n",
                m.group(2), m.group(1));

        if (!this.listofTables.containsKey(m.group(1))) {
            return "ERROR: You are trying to insert into a non existent table";
        }
        //1. Retrieve the table that we are going to insert into
        Table x = this.listofTables.get(m.group(1));

        //2. Split the string literal by commas, into an object array.
        String s = m.group(2);
//        s = s.replaceAll("\\s", "");

        Object[] objectArray = s.split(",");
        Object[] rowtobeAdded = new Object[objectArray.length];

        //3. Based on the columnn type, try to parse the fucking thing
        for (int i = 0; i < objectArray.length; i++) {
            Column currentCol = (Column) x.am.get(x.headers[i]);
            String y = (String) objectArray[i];
            if (y.contains("NOVALUE")) {
                y = y.replaceAll("'", "");
                rowtobeAdded[i] = y;
            } else if (currentCol.columnType.equals("String") && y.contains("'")) {
                y = y.replaceAll("'", "");
                rowtobeAdded[i] = y;

            } else if (currentCol.columnType.equals("int") && !y.contains(".")) {
                try {
                    Integer result = Integer.parseInt(y);
                    rowtobeAdded[i] = result;
                } catch (NumberFormatException e) {
                    return "ERROR: wrong type";
                }

            } else if (currentCol.columnType.equals("float") && y.contains(".")) {
                try {
                    Float result = Float.parseFloat(y);
                    String z = String.format("%.3g%n", result);
                    Float ret = Float.parseFloat(z);
                    rowtobeAdded[i] = ret;
                } catch (NumberFormatException e) {
                    return "ERROR: wrong type";
                }
            } else {
                return "ERROR: Cannot parse";
            }

        }
        x.addRow(objectArray);
        return "";
    }

    private String storeTable(String name) {
        System.out.printf("You are trying to store the table named %s\n", name);

        if (!this.listofTables.containsKey(name)) {
            return "ERROR: The table you are trying to store is nonexistent";
        }
        String fileName = name + ".tbl";
        File file = new File(fileName);
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
//            String content = "This is the content to write into file\n";
            String content = printTable(name);

            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(content);


        } catch (IOException e) {

            return "ERROR: Could not store table.";

        } finally {

            try {

                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return "";
    }

    private String dropTable(String name) {
        System.out.printf("You are trying to drop the table named %s\n", name);
        try {
            if (!this.listofTables.containsKey(name)) {
                return "ERROR: The table does not exist, unable to drop it";
            } else {
                this.listofTables.remove(name);
                return "";
            }
        } catch (NullPointerException nullPointer) {
            return "ERROR: Null pointer encountered";
        }

    }

    private String loadTable(String name) {
        System.out.printf("You are trying to load the table named %s\n", name);
        String fileName = name + ".tbl";
        File file = new File(fileName);
        try {
            Scanner s = new Scanner(file);
            //read the first line, put it as table header.
            String x = s.nextLine();
            createTable(name + " (" + x + ")");
            Table currentTable = this.listofTables.get(name);
            while (s.hasNextLine()) {
                String y = s.nextLine();
                String[] yArray = y.split(",");
                //a proper row should have as many columns as the header
                if (yArray.length != currentTable.am.size()) {
                    return "ERROR: Table is malformed";
                }
                currentTable.addRow(yArray);
            }
        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException e) {
            return "ERROR: File not found";
        }


        return "";
    }

    private String printTable(String name) {
        System.out.printf("You are trying to print the table named %s\n", name);
        try {
            Table x = this.listofTables.get(name);
            return x.tablePrint();
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return "ERROR: The table is bad / does not exist";
        }
    }

    private String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            return "ERROR: Malformed create";
        }
    }

    private String createNewTable(String name, String[] cols) {
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < cols.length - 1; i++) {
            joiner.add(cols[i]);
        }
        String colSentence = joiner.toString() + " and " + cols[cols.length - 1];
        System.out.printf("u tryna cre8 table named %s with the cols %s\n", name, colSentence);
        for (int i = 0; i < cols.length; i++) {
            cols[i] = cols[i].trim().replaceAll(" +", " ");
        }

        //loop to check 2nd word of header to see if its string, int or float.
        //If it isnt, return an error message.
        for (int i = 0; i < cols.length; i++) {
            String[] x = cols[i].split(" ");
            if (!x[1].equals("string") && !x[1].equals("int") && !x[1].equals("float")) {
                return "ERROR: Invalid header type.";
            }
        }

        numberofTables++;
        Table x = new Table(name, cols);
        this.listofTables.put(name, x);
        return "";
    }

    private String createSelectedTable(String name, String exp, String tables, String conds) {
        System.out.printf("U tryna create a table named %s by selecting these expressions:"
                + " '%s' from the join of these tables: '%s', "
                + "filtered by these conditions: '%s'\n", name, exp, tables, conds);
        Table ret = select(exp, tables, conds);
        numberofTables++;
        this.listofTables.put(name, ret);
        return "";
    }

    private String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            //System.err.printf("Malformed select: %s\n", expr);
            return "ERROR: Malformed select";
        }

        return select(m.group(1), m.group(2), m.group(3)).tablePrint();
    }

    private String[] columnArray(String column) {
        String[] tempStringArray = column.split(" as ");
        String[] length4stringArray = new String[4];
        length4stringArray[3] = tempStringArray[1];
        if (tempStringArray[0].contains("+")) {
            length4stringArray[1] = "+";
            String[] temp2 = tempStringArray[0].split("\\+");
            length4stringArray[0] = temp2[0];
            length4stringArray[2] = temp2[1];
        } else if (tempStringArray[0].contains("-")) {
            length4stringArray[1] = "-";
            String[] temp2 = tempStringArray[0].split("-");
            length4stringArray[0] = temp2[0];
            length4stringArray[2] = temp2[1];
        } else if (tempStringArray[0].contains("*")) {
            length4stringArray[1] = "*";
            String[] temp2 = tempStringArray[0].split("\\*");
            length4stringArray[0] = temp2[0];
            length4stringArray[2] = temp2[1];
        } else if (tempStringArray[0].contains("/")) {
            length4stringArray[1] = "/";
            String[] temp2 = tempStringArray[0].split("/");
            length4stringArray[0] = temp2[0];
            length4stringArray[2] = temp2[1];
        }
        for (int j = 0; j < 4; j++) {
            length4stringArray[j] = length4stringArray[j].replaceAll("\\s", "");
            length4stringArray[j] = length4stringArray[j].trim();
        }
        return length4stringArray;
    }

    private String columnName(String[] length4stringArray, Table masterTable) {
        String withType = (String) masterTable.mapHeaders.get(length4stringArray[0]);
        String withType2 = (String) masterTable.mapHeaders.get(length4stringArray[2]);
        //Do a comparison for handling int + float cases.
        if (withType.contains("int") && withType2.contains("float")) {
            withType = withType2.replace(length4stringArray[2], length4stringArray[3]);
            withType = withType.trim();
        } else {
            withType = withType.replace(length4stringArray[0], length4stringArray[3]);
            withType = withType.trim();
        }

        return withType;
    }

    private void stringArithmetic(Column toReturn, Column a,
                                  Column b, int i, String[] length4stringArray) {
        String novaluecheck1 = (String) a.get(i);
        String novaluecheck2 = (String) b.get(i);
        String x = "";
        if (!novaluecheck1.contains("NOVALUE")) {
            x = (String) a.get(i);
        }
        String y = "";
        if (!novaluecheck2.contains("NOVALUE")) {
            y = (String) b.get(i);
        }
        y = y.replaceAll("'", "");
        x = x.replaceAll("'", "");
        if (length4stringArray[1].equals("+")) {
            if (x.contains("NOVALUE") || y.contains("NOVALUE")) {
                toReturn.add("");
            } else if (x.contains("NOVALUE")) {
                toReturn.add(y);
            } else if (y.contains("NOVALUE")) {
                toReturn.add(x);
            } else {
                String z = x.concat(y);
                z = "'" + z + "'";
                toReturn.add(z);
            }
        }
    }

    private Column asColCreator(String[] length4stringArray, Table masterTable, String name) {
        Column<Object> toReturn = new Column<>();
        Column col1 = (Column) masterTable.am.get(masterTable.headers[0]);
        for (int i = 0; i < col1.size(); i++) {
            String col1withoutType = length4stringArray[0].replaceAll("\\s", "");
            String col2withoutType = length4stringArray[2].replaceAll("\\s", "");
            String col1withType = (String) masterTable.mapHeaders.get(col1withoutType);
            String col2withType = (String) masterTable.mapHeaders.get(col2withoutType);
            Column a = (Column) masterTable.am.get(col1withType);
            Column b = (Column) masterTable.am.get(col2withType);
            if (name.contains("string")) {
                stringArithmetic(toReturn, a, b, i, length4stringArray);
            } else if (name.contains("int")) {
                String novaluecheck1 = (String) a.get(i);
                String novaluecheck2 = (String) b.get(i);
                int x = 0;
                if (!novaluecheck1.contains("NOVALUE")) {
                    x = Integer.valueOf((String) a.get(i));
                }
                int y = 0;
                if (!novaluecheck2.contains("NOVALUE")) {
                    y = Integer.valueOf((String) b.get(i));
                }
                if (length4stringArray[1].equals("+")) {
                    toReturn.add(x + y);
                } else if (length4stringArray[1].equals("-")) {
                    toReturn.add(x - y);
                } else if (length4stringArray[1].equals("*")) {
                    toReturn.add(x * y);
                } else if (length4stringArray[1].equals("/")) {
                    if (y == 0) {
                        toReturn.add(Float.NaN);
                    } else {
                        toReturn.add(x / y);
                    }
                }
            } else if (name.contains("float")) {
                String novaluecheck1 = (String) a.get(i);
                String novaluecheck2 = (String) b.get(i);
                float x = 0;
                if (!novaluecheck1.contains("NOVALUE")) {
                    x = Float.valueOf((String) a.get(i));
                }
                float y = 0;
                if (!novaluecheck2.contains("NOVALUE")) {
                    y = Float.valueOf((String) b.get(i));
                }
                if (length4stringArray[1].equals("+")) {
                    toReturn.add(x + y);
                } else if (length4stringArray[1].equals("-")) {
                    toReturn.add(x - y);
                } else if (length4stringArray[1].equals("*")) {
                    toReturn.add(x * y);
                } else if (length4stringArray[1].equals("/")) {
                    if (y == 0) {
                        toReturn.add(Float.NaN);
                    } else {
                        toReturn.add(x / y);
                    }
                }
            }
        }
        return toReturn;
    }

    private Table singleSelect(String[] arrayofTables, String[] columns,
                               String[] withTypearray, String exprs) {
        Table masterTable = listofTables.get(arrayofTables[0]);
        if (exprs.equals("*")) {
            String ret = masterTable.tablePrint();
            return masterTable;
        }
        //if (exprs.contains(" as ")) {
        //    return asSelect(columns, masterTable);
        //}
        LinkedList<String[]> listof4s = new LinkedList<>();
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].contains(" as ")) {
                String[] length4stringArray = columnArray(columns[i]);
                withTypearray[i] = columnName(length4stringArray, masterTable);
                listof4s.add(length4stringArray);
            } else {
                columns[i] = columns[i].replaceAll("\\s", "");
                String withType = (String) masterTable.mapHeaders.get(columns[i]);
                withTypearray[i] = withType;
            }
        }
        Table createdTable = new Table("newTable", withTypearray);
        int counter = 0;
        int counter4 = 0;
        while (counter < columns.length) {
            String[] temp = withTypearray[counter].split(" ");

            if (columns[counter].contains(" as ")) {
                if (withTypearray[counter].contains("string")) {
                    if (!listof4s.get(counter4)[1].equals("+")) {
                        throw new RuntimeException("ERROR: Can't do that");
                    }
                }
                Column newCol = asColCreator(listof4s.get(counter4),
                        masterTable, withTypearray[counter]);
                Column extraNew = (Column) newCol.clone();
                extraNew.columnType = temp[1];
//                createdTable.mapHeaders.put(temp[0], withTypearray[0]);
                createdTable.am.put(withTypearray[counter], extraNew);
                counter4++;
            } else {
                Column coltobeCopied = (Column) masterTable.am.get(withTypearray[counter]);
                Column newCol = (Column) coltobeCopied.clone();
                newCol.columnType = temp[1];
//                createdTable.mapHeaders.put(temp[0], withTypearray[0]);
                createdTable.am.put(withTypearray[counter], newCol);
            }
            counter++;
        }
        return createdTable;
    }

    private Table dupleSelect(LinkedList stringList) {
        if (stringList.size() == 2) {
            Table firstTable = listofTables.get(stringList.get(0));
            Table secondTable = listofTables.get(stringList.get(1));
            Table joinedTable = Table.Join(firstTable, secondTable);


            stringList.addFirst(joinedTable.tableName);
            return joinedTable;
        } else {
            LinkedList<String> tempList = new LinkedList<>();
            tempList.add((String) stringList.removeFirst());
            tempList.add((String) stringList.removeFirst());
            listofTables.put("tempTable", dupleSelect(tempList));
            stringList.addFirst("tempTable");
            return dupleSelect(stringList);
        }
    }

    private Table select(String exprs, String tables, String conds) {
        System.out.printf("You are trying to select these expressions:"
                +
                " '%s' from the join of these tables: '%s', filtered by these conditions: "
                +
                "'%s'\n", exprs, tables, conds);


        String[] arrayofTables = tables.split(",");
        LinkedList<String> stringList = new LinkedList<String>(Arrays.asList(arrayofTables));
        String[] columns = exprs.split(",");
        String[] withTypearray = new String[columns.length];
        Table currentTable;

        if (stringList.size() == 1) {
            currentTable = singleSelect(arrayofTables, columns, withTypearray, exprs);
        } else {
            currentTable = dupleSelect(stringList);
        }
        if (conds == null) {
            return currentTable;
        } else {
            return doConds(currentTable, conds);
        }

    }

    private Table doConds(Table currentTable, String conds) {
        String[] condsArray = conds.split(" and ");
        LinkedList<String[]> condsList = new LinkedList<>();
        for (int i = 0; i < condsArray.length; i++) {
            condsList.add(splitConds(condsArray[i]));
        }
        Column col1 = (Column) currentTable.am.get(currentTable.headers[0]);
        for (int i = col1.size() - 1; i >= 0; i--) {
            for (int j = 0; j < condsList.size(); j++) {
                if (!checkConds(condsList.get(j), i, currentTable)) {
                    for (int k = 0; k < currentTable.headers.length; k++) {
                        ((Column) currentTable.am.get(currentTable.headers[k])).remove(i);
                    }
                    j = condsList.size();
                }
            }
        }
        return currentTable;
    }

    private String[] splitConds(String conds) {
        String[] length3stringArray = new String[3];
        if (conds.contains("<")) {
            length3stringArray[1] = "<";
            String[] temp2 = conds.split("<");
            length3stringArray[0] = temp2[0];
            length3stringArray[2] = temp2[1];
        } else if (conds.contains(">")) {
            length3stringArray[1] = ">";
            String[] temp2 = conds.split(">");
            length3stringArray[0] = temp2[0];
            length3stringArray[2] = temp2[1];
        } else if (conds.contains("<=")) {
            length3stringArray[1] = "<=";
            String[] temp2 = conds.split("<=");
            length3stringArray[0] = temp2[0];
            length3stringArray[2] = temp2[1];
        } else if (conds.contains(">=")) {
            length3stringArray[1] = ">=";
            String[] temp2 = conds.split(">=");
            length3stringArray[0] = temp2[0];
            length3stringArray[2] = temp2[1];
        } else if (conds.contains("==")) {
            length3stringArray[1] = "==";
            String[] temp2 = conds.split("==");
            length3stringArray[0] = temp2[0];
            length3stringArray[2] = temp2[1];
        } else if (conds.contains("!=")) {
            length3stringArray[1] = "!=";
            String[] temp2 = conds.split("!=");
            length3stringArray[0] = temp2[0];
            length3stringArray[2] = temp2[1];
        }
        for (int j = 0; j < 3; j++) {
            length3stringArray[j] = length3stringArray[j].replaceAll("\\s", "");
            length3stringArray[j] = length3stringArray[j].trim();
        }
        return length3stringArray;
    }


    private boolean checkConds(String[] condsArray, int index, Table currentTable) {
        Column col1 = (Column) currentTable.am.get(currentTable.mapHeaders.get(condsArray[0]));
        Column col2 = (Column) currentTable.am.get(currentTable.mapHeaders.get(condsArray[2]));
        if (((String) currentTable.mapHeaders.get(condsArray[0])).contains("string")) {
            String x = (String) col1.get(index);
            String y = (String) col2.get(index);
            if (condsArray[1].equals("==")) {
                if (x.equals(y)) {
                    return true;
                }
            } else if (condsArray[1].equals("!=")) {
                if (!x.equals(y)) {
                    return true;
                }
            } else if (condsArray[1].equals("<")) {
                if (x.compareTo(y) == -1) {
                    return true;
                }
            } else if (condsArray[1].equals("<=")) {
                if (x.compareTo(y) <= 0) {
                    return true;
                }
            } else if (condsArray[1].equals(">")) {
                if (x.compareTo(y) == 1) {
                    return true;
                }
            } else if (condsArray[1].equals(">=")) {
                if (x.compareTo(y) >= 0) {
                    return true;
                }
            }
        } else {
            String a = (String) col1.get(index);
            String b = (String) col2.get(index);
            float x = Float.valueOf(a);
            float y = Float.valueOf(b);
            if (condsArray[1].equals("==")) {
                if (x == y) {
                    return true;
                }
            } else if (condsArray[1].equals("!=")) {
                if (x != y) {
                    return true;
                }
            } else if (condsArray[1].equals("<")) {
                if (x < y) {
                    return true;
                }
            } else if (condsArray[1].equals(">")) {
                if (x > y) {
                    return true;
                }
            } else if (condsArray[1].equals("<=")) {
                if (x <= y) {
                    return true;
                }
            } else if (condsArray[1].equals(">=")) {
                if (x >= y) {
                    return true;
                }
            }
        }
        return false;
    }


}
