namespace WorldsFactory.world.ids;

public interface ITagManager
{
    void DeleteObject(TaggedConcept obj);
    public void newTaggedObjectCreated(TaggedConcept obj);
    public void AddTag(TaggedConcept obj, string tag);
    public void RemoveTag(TaggedConcept obj, string tag);
}