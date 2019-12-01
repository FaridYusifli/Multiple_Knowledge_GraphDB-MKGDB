package mkgdb.graph;

public interface IRelationCollector
{
    public boolean open();
    public void close();

    public void add(NodeRelation nodeRelation);
    public void add(Iterable<NodeRelation> nodeRelations);
}