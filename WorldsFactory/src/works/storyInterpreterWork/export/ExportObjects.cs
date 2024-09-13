using System.Data;
using System.Text;
using Serilog;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.library.classStructure.types;
using WorldsFactory.world.objects;

namespace WorldsFactory.works.storyInterpreterWork.export;

public class ExportObjects
{
	private IObjects objects;

	public ExportObjects(IObjects objects)
	{
		this.objects = objects;
	}

	public void Export(out StringBuilder code)
	{
		validateObjectTypes();
		code = new StringBuilder(@"
# ------------------- LIBRARY OBJECTS -------------------

class Objects:
	_instance = None
	
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(Objects, cls).__new__(cls)		
");

		foreach (var object_ in objects.Collection)
		{
			export_object(object_, out var objectsCode);
			code.Append(objectsCode);
		}


		code.Append("\t\ttry:\n");
		foreach (var object_ in objects.Collection)
		{
			code.Append("\t\t\tcls." + object_.Name + "_initialize(cls." + object_.Name + ")\n");
		}
		if (objects.Collection.Count == 0)
		{
			code.Append("\t\t\tpass\n");
		}
		code.Append("\t\texcept Exception as e:\n");
		code.Append("\t\t\tprint(\"_exception_\")\n");
		code.Append("\t\t\tprint(\"Error in python code, while initializing objects:\" + str(e))\n");
		code.Append("\t\t\tprint(\"_exception_end_\")\n");
		code.Append("\t\t\traise MethodRuntimeException(\"An Exception in an init method\")\n");
		code.Append("\t\treturn cls._instance\n");
		code.Append("\t\tpass\n");

		foreach (var object_ in objects.Collection)
		{
			code.Append("\t\n");
			shift_methodsBody(object_.InitMethod, out var initMethodCode);
			code.Append(initMethodCode);
		}

		export_getAllObjectNamesOfType_method(out var getAllObjectNamesOfTypeCode);
		code.Append(getAllObjectNamesOfTypeCode);

		code.Append("\nobjects = Objects()\n\n\n");
		Log.Information(code.ToString());
	}

	private void shift_methodsBody(IMethod method, out StringBuilder code)
	{
		code = new StringBuilder();
		var methodsBody = method.Body!.Code.Split("\n");
		foreach (var line in methodsBody)
		{
			code.Append("\t" + line + "\n");
		}
	}

	private void export_object(WFObject object_, out StringBuilder code)
	{
		code = new StringBuilder("\t\tcls." + object_.Name + " = ");
		code.Append("class_" + object_.Type.GetPostfixId + "(\"" + object_.Id + "\")\n");
		code.Append("\t\tcls." + object_.Name + ".__object_name__ = \"" + object_.Name + "\"\n");
		// for each array property call set_objects_name
		if(object_.Type is Reference<IClass> classRef)
		{
			var class_ = (IClass?)classRef.GetConceptWithId();
			if (class_ is null)
				return;
				
			foreach (var property in class_.Properties)
			{
				if (property.StringArrayBrackets != "")
				{
					code.Append("\t\tcls." + object_.Name + "." + property.Name + ".set_objects_name(\"" + object_.Name + "\")\n");
				}
			}
			
			Class? parent = class_.Parent?.TryGetConcept() as Class;
			if (class_.Parent is not null && parent is null)
				throw new ConceptNotFoundException("Parent of class " + class_.Id + " cannot be found.");

			// export all array properties from the parent classes
			while (parent is not null)
			{
				foreach (var property in parent.Properties)
				{
					if (property.StringArrayBrackets != "")
					{
						code.Append("\t\tcls." + object_.Name + "." + property.Name + ".set_objects_name(\"" + object_.Name + "\")\n");
					}
				}
				parent = parent.Parent?.TryGetConcept() as Class;
			}
		}
		
	}

	private void validateObjectTypes()
	{
		foreach (var object_ in objects.Collection)
		{
			if (object_.Type is Reference<IClass> reference)
			{
				if (reference.GetConceptWithId() is null)
				{
					throw new ExportWholeWorld.SyntaxErrorException(0, 0, "", "", "Object " + object_.Name + " has invalid type");
				}
			}
		}
	}

	private void export_getAllObjectNamesOfType_method(out StringBuilder code)
	{
		code = new StringBuilder();
		code.Append("""
	
	def get_all_object_names_of_type(cls, type_str):
		object_names = []
		# Convert the type string to an actual type object
		try:
			desired_type = eval(type_str)
		except NameError:
			logging.warning(f"Type '{type_str}' is not recognized.")
			return object_names
		for name in dir(cls):
			attr = getattr(cls, name)
			# Check if the attribute is an instance of the desired type
			if isinstance(attr, desired_type):
				object_names.append(name)
		# print all in format name1,name2,name3...
		return ",".join(object_names)

""");
	}
}