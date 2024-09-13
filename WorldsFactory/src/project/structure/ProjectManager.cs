namespace WorldsFactory.project;

/// <summary>
/// Entry point to the project management.
/// </summary>
public class ProjectManager
{
	/// <summary>
	/// All known projects.
	/// </summary>
	public AllProjects AllProjects { get; init; }
	public ProjectActions ProjectActions { get; init; }
	
	/// <summary>
	/// Currently opened projects.
	/// </summary>
	public HashSet<ICurrentlyOpenedProject> CurrentProjects { get; init; }
	public ProjectManager()
	{
		CurrentProjects = new HashSet<ICurrentlyOpenedProject>();
		AllProjects = new AllProjects();
		ProjectActions = new ProjectActions(AllProjects);
	}
}