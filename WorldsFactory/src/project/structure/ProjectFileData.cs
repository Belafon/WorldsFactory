using Newtonsoft.Json;

namespace WorldsFactory.project;

/// <summary>
/// Persists project file data. 
/// <see cref="ProjectFile"/> 
/// </summary>
public class ProjectFileData
{
	public ProjectFileData(string name)
	{
		Name = name;
	}
	
	[JsonProperty("name")]
	public string Name { get; private set; }
	
	public static bool validate(ProjectFileData? project, string error){
		error = "";
		if (project == null || string.IsNullOrWhiteSpace(project.Name))
		{
			error = "Invalid project data in the project file.";
			return false;
		}
		return true;
	}
}