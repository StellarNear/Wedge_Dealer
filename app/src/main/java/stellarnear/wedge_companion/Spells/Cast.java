package stellarnear.wedge_companion.Spells;

public class Cast {  //objet fait pour detecter des cast entre spell binded
    private boolean cast;
    private boolean failed;
    private boolean rmPassed;

    public Cast() {
        cast = false;
    }

    public void cast() {
        this.cast = true;
    }

    public void setFailed() {
        this.failed = true;
    }

    public void setRmPassed() {
        this.rmPassed = true;
    }

    public boolean hasPassedRM() {
        return this.rmPassed;
    }

    public boolean isCast() {
        return cast;
    }

    public boolean isFailed() {
        return failed;
    }
}
