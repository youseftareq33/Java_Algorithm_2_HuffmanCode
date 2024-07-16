package B_Classes;

public class Code implements Comparable<Code> {

    char ch;
    String code;
    int rep;
    int length;

    public Code () {

    }

    public Code(char ch, String code,int rep) {
        this.ch = ch;
        this.code = code;
        this.rep = rep;
        length = this.code.length();
    }

    public int compareTo(Code c) {
        if (c.ch > this.ch)
            return -1;
        else if (c.ch < this.ch)
            return 1;
        else
            return 0;
    }

    public String toString() {
        return (int)ch + " : " + "rep : " + this.rep +" Code : " +  code + "\n";
    }

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
