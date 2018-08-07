package db;

import Map61B.ArrayMap;

import java.util.HashMap;

import java.util.Arrays;
import java.util.LinkedList;


/**
 * Created by Clarkson on 18/2/17.
 */
public class Table {
    String tableName;
    HashMap am;
    String[] headers;
    HashMap mapHeaders;

    //Table constructor
    //Creates an empty table with just the table headers
    public Table(String name, String[] listofKeys) {
        this.headers = new String[listofKeys.length];
        this.tableName = name;
        this.am = new HashMap<String, Column>();
        this.mapHeaders = new HashMap<String, String>();

        for (int i = 0; i < listofKeys.length; i++) {
            Column newCol = new Column();
            String[] x = listofKeys[i].split(" ");

            mapHeaders.put(x[0], listofKeys[i]);

            if (x.length > 1) {
                if (x[1].equals("string")) {
                    newCol.columnType = "String";

                } else if (x[1].equals("int")) {
                    newCol.columnType = "int";


                } else if (x[1].equals("float")) {
                    newCol.columnType = "float";

                }
            }
            am.put(listofKeys[i], newCol);
            headers[i] = listofKeys[i];
        }

    }


    public void addRow(Object[] rowtobeAdded) {
        //go through each column (linked list) in the column array of the map, append to each
        for (int i = 0; i < this.headers.length; i++) {
            Column coltobeAppended = (Column) this.am.get(headers[i]);
            coltobeAppended.add(rowtobeAdded[i]);
        }
    }

    public String tablePrint() {
        StringBuffer stringRep = new StringBuffer();
        Column c1 = (Column) this.am.get(this.headers[0]);

        for (int i = 0; i < this.headers.length; i++) {
            if (i == 0) {
                stringRep.append(headers[i]);
                if (i == this.headers.length - 1) {
                    stringRep.append("\n");
                }

            } else {
                stringRep.append("," + headers[i]);
                if (i == this.headers.length - 1) {
                    stringRep.append("\n");
                }
            }
        }


        for (int i = 0; i < c1.size(); i++) {
            for (int j = 0; j < this.headers.length; j++) {
                Column currentC = (Column) this.am.get(this.headers[j]);
                if (j == 0) {
                    if (currentC.columnType.equals("float")) {
                        String a = currentC.get(i).toString();
                        if (a.contains("NOVALUE")) {
                            stringRep.append("NOVALUE");
                        } else {
                            float x = Float.valueOf(a);
                            stringRep.append(String.format("%.3f", x));
                        }
                    } else {
                        stringRep.append(currentC.get(i));
                    }
                    if (j == this.headers.length - 1 && i != c1.size() - 1) {
                        stringRep.append("\n");
                    }
                } else {
                    if (currentC.columnType.equals("float")) {
                        String a = currentC.get(i).toString();
                        if (a.contains("NOVALUE")) {
                            stringRep.append(",NOVALUE");
                        } else {
                            float x = Float.valueOf(a);
                            stringRep.append("," + String.format("%.3f", x));
                        }
                    } else {
                        stringRep.append("," + currentC.get(i));
                    }
                    if (j == this.headers.length - 1 && i != c1.size() - 1) {
                        stringRep.append("\n");
                    }
                }
            }
        }
        return stringRep.toString();
    }


    //firstCompare returns an array of common headers between 2 tables.
    private LinkedList<String> getMatchingKeys(Table t2) {
        LinkedList<String> matchedKeys = new LinkedList<>();
        for (int i = 0; i < this.headers.length; i++) {
            for (int j = 0; j < t2.headers.length; j++) {
                if (this.headers[i].equals(t2.headers[j])) {
                    matchedKeys.addLast(this.headers[i]);
                }
            }
        }
        return matchedKeys;
    }

    private Table joinEmptyTableMaker(Table t2) {
        LinkedList<String> matchedKeysList = this.getMatchingKeys(t2);
        String[] x = this.headers;
        LinkedList headersList1 = new LinkedList(Arrays.asList(x));
        String[] y = t2.headers;
        LinkedList headersList2 = new LinkedList(Arrays.asList(y));
        for (int i = 0; i < matchedKeysList.size(); i++) {
            headersList1.remove(matchedKeysList.get(i));
            headersList2.remove(matchedKeysList.get(i));
        }
        matchedKeysList.addAll(headersList1);
        matchedKeysList.addAll(headersList2);
        String[] newHeader = matchedKeysList.toArray(new String[matchedKeysList.size()]);

        Table t3 = new Table("t3", newHeader);
        return t3;
    }

    private Table populatejoinEmptyTable(Table t2) {
        Table emptyTable = this.joinEmptyTableMaker(t2);
        LinkedList<String> x = this.getMatchingKeys(t2);
        String[] matchingKeys = x.toArray(new String[x.size()]);
        Column sampleCol1 = (Column) this.am.get(this.headers[0]);
        Column sampleCol2 = (Column) t2.am.get(t2.headers[0]);
        for (int i = 0; i < sampleCol1.size(); i++) {
            for (int j = 0; j < sampleCol2.size(); j++) {
                if (x.isEmpty()) {
                    LinkedList cartesianRowAdd = new LinkedList();
                    for (int l = 0; l < this.headers.length; l++) {
                        Column col = (Column) this.am.get(emptyTable.headers[l]);
                        cartesianRowAdd.addLast(col.get(i));
                    }
                    for (int m = 0; m < t2.headers.length - matchingKeys.length; m++) {
                        Column col = (Column) t2.am.get(emptyTable.headers[this.headers.length + m]);
                        cartesianRowAdd.addLast(col.get(j));
                    }
                    Object[] rowAdded = (Object[]) cartesianRowAdd.toArray(new Object[cartesianRowAdd.size()]);
                    emptyTable.addRow(rowAdded);


                } else {
                    for (int k = 0; k < matchingKeys.length; k++) {
                        Column a = (Column) this.am.get(matchingKeys[k]);
                        Object y = a.get(i);
                        Column b = (Column) t2.am.get(matchingKeys[k]);
                        Object z = b.get(j);

                        if (y.equals(z)) {
                            if (k == matchingKeys.length - 1) {
                                LinkedList rowtobeAdded = new LinkedList();
                                for (int l = 0; l < this.headers.length; l++) {
                                    Column col = (Column) this.am.get(emptyTable.headers[l]);
                                    rowtobeAdded.addLast(col.get(i));
                                }
                                for (int m = 0; m < t2.headers.length - matchingKeys.length; m++) {
                                    Column col = (Column) t2.am.get(emptyTable.headers[this.headers.length + m]);
                                    rowtobeAdded.addLast(col.get(j));
                                }
                                Object[] rowAdded = (Object[]) rowtobeAdded.toArray(new Object[rowtobeAdded.size()]);
                                emptyTable.addRow(rowAdded);

                            } else {
                                continue;//store the i and j rows into a nice array, then call addRow on the emptyTable.
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return emptyTable;
    }


    public static Table Join(Table t1, Table t2) {
        return t1.populatejoinEmptyTable(t2);
    }

    public static void main(String[] args) {
        String name = "table1";
        String[] listofKeys = new String[]{"X int", "Y int"};
        Table t1 = new Table(name, listofKeys);
        Table t2 = new Table("table2", new String[]{"X int", "Z int"});
        t1.addRow(new Object[]{"a", 4});
        t1.addRow(new Object[]{"b", 5});
        t1.addRow(new Object[]{"c", 6});

        t2.addRow(new Object[]{"a", 7});
        t2.addRow(new Object[]{"d", 7});
        t2.addRow(new Object[]{"a", 9});
        t2.addRow(new Object[]{"a", 11});
        Table t3 = Join(t1, t2);
        t3.tablePrint();

    }
}

