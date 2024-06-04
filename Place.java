import java.util.Arrays;

public class Place {

    String cid;
    String file;
    int s;
    String year;
    String title;
    double[] geo;

    public String getCid() {
        return cid;
    }

    @Override
    public String toString() {
        return "New.Place{" +
                "cid='" + cid + '\'' +
                ", file='" + file + '\'' +
                ", s=" + s +
                ", year='" + year + '\'' +
                ", title='" + title + '\'' +
                ", geo=" + Arrays.toString(geo) +
                '}';
    }
}