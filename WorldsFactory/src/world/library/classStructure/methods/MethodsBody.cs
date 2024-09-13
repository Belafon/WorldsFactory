using System.Threading.Tasks;
using System.Collections.ObjectModel;
using System.Text;
using Avalonia.Platform.Storage;
using System.ComponentModel;
using Serilog;
using System.Text.Json.Serialization;
using WorldsFactory.world.objects;
using System.Text.RegularExpressions;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.library.classStructure;

public class MethodsBody : IMethodsBody
{
	private IMethodsBodyLoader loader;
	private string code;

	public event PropertyChangedEventHandler? PropertyChanged;

	public string Code
	{
		set
		{
			if (code == value)
				return;
			code = value;
			loader.Save(code);
			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("Code"));
			findAllObjectReferences();
		}
		get => code;
	}
	public MethodsBody(IMethodsBodyLoader loader, string? code = null)
	{
		this.code = "";
		this.loader = loader;

		if (code != null)
			this.code = code;
		else loadCode();

		findAllObjectReferences();
	}

	private async void loadCode()
	{
		var code = await loader.Load();
		if (code != null)
			Code = code;
		else
		{
			Log.Error("Error while loading methods body code, methods body will be empty.");
			Code = "";
		}
	}


	public static string GetMethodsHeader(string name, List<Parameter> parameters)
	{
		StringBuilder sb = new StringBuilder();
		sb.Append("def " + name + "(");
		for (int i = 0; i < parameters.Count - 1; i++)
		{
			sb.Append(parameters[i].Name + ", ");
		}

		if (parameters.Count > 0)
		{
			sb.Append(parameters[^1].Name + "):");
		}
		else
		{
			sb.Append("):");
		}

		return sb.ToString();
	}

	public void Delete()
	{
		loader.DeleteMethodsBodyFile();
	}

	[JsonIgnore]
	public ObservableCollection<WFObject> ReferencesToObjects { get; } = new();

	private void findAllObjectReferences()
	{
		// foreach line with objects."name"
		foreach (var line in Code.Split('\n'))
		{
			var regexFindObjectName = new Regex(@"objects\.[a-zA-Z0-9_]+");

			// go through all matches
			foreach (Match match in regexFindObjectName.Matches(line))
			{
				// get the name of the object
				var objectName = match.Value.Split('.')[1];
				var objectId = "@object:" + objectName;
				if (ReferencesToObjects.Any(obj => obj.Name == objectName))
					continue;

				var objRef = new Reference<WFObject>(objectId, loader.IdManager);
				var obj = (WFObject)objRef.TryGetConcept()!;
				if (obj != null)
					ReferencesToObjects.Add(obj);
				else
				{
					objRef.OnConceptFound += (sender, args) =>
					{
						if (ReferencesToObjects.Any(o =>
						{
							var oName = o.Id.Split(':')[1];
							return oName == objectName;
						}))
							return;
						// TODO BUG null Name of new wfobject

						ReferencesToObjects.Add((WFObject)objRef.TryGetConcept()!);
					};
				}
			}
		}
	}
}
