namespace WorldsFactory.works;


/// <summary>
/// Represents concrete type of work, 
/// it can be a book, screenplay, gamebook, etc.
/// </summary>
public interface IWork
{
	public string Name { get; set; }
	public void Delete();
	public event EventHandler? OnDelete;
}
