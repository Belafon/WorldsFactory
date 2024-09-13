namespace WorldsFactory.works;

/// <summary>
/// Represents concrete type of work, 
/// it can be a book, screenplay, gamebook, etc.
/// </summary>
public class Work : IWork
{
	public event EventHandler? OnDelete;
	private IWorkLoader loader;
	public string Name { get; set; }
	public const string ID_PREFIX = "@work:";

	/// <summary>
	/// Represents concrete type of work, 
	/// it can be a book, screenplay, gamebook, etc.
	/// </summary>
	public Work(IWorkLoader loader, string name)
	{
		this.loader = loader;
		this.Name = name;
	}

	public void Delete()
	{
		loader.Delete(this);
		OnDelete?.Invoke(this, EventArgs.Empty);
	}
}
