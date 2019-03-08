package func;

public class FileToken extends Token {

    String value;
    int column;
    int row;
    int length;

    public FileToken(Type t, String value, int column, int row, int length) {
        super(t);
        this.value = value;
        this.column = column + 1;
        this.row = row + 1;
        this.length = length;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
            .append(this.type).append(" ");
        if (this.value != null) builder.append(this.value);
        builder.append(" [").append(this.row).append(":").append(this.column);
        builder.append("-").append(this.column + this.length);
        builder.append("]");
        return builder.toString();
    }
}
