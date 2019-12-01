package mkgdb.graph;

import java.io.IOException;
import java.util.function.Function;

public interface IExtractor
{
    public void extractTo(IRelationCollector collector) throws IOException;
    public IExtractor setNameFixer(Function<String, String> nameFixer);
}