using System.ComponentModel;

namespace WorldsFactory.world.ids;

public interface  IConceptWithID
{
	public string Id { get; set; }
    public string PostfixId { get; }
    public event PropertyChangedEventHandler? OnIdChanged;
}