package drawcommand;

/**
 * Created by jiftech on 2015/07/08.
 */
public abstract class ChangeCommand implements Command{
    @Override
    public boolean isDrawCommand(){
        return false;
    }
}
