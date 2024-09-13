using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.works.storyInterpreterWork.export;

public class ExportClasses
{
	private ILibrary library;

	public ExportClasses(ILibrary library)
	{
		this.library = library;
	}

	public void Export(out StringBuilder code)
	{
		code = new StringBuilder("# ------------------- LIBRARY CLASSES -------------------\n\n");

		code.Append("""
class MethodRuntimeException(Exception):
	def __init__(self, message="An exception occurred during method execution"):
		self.message = message
		super().__init__(self.message)


""");
		foreach (var class_ in library.Classes)
		{
			if (class_.Parent is null)
			{
				ExportClass(class_, out StringBuilder classCode);
				code.Append(classCode + "\n\n");
				foreach (var childRef in class_.Children)
				{
					var child = childRef.TryGetConcept() as Class;
					if (child is null)
						continue;

					exportChildClass(child, class_, code);
				}
			}
		}
	}

	private void exportChildClass(Class child, IClass class_, StringBuilder code)
	{
		ExportClass(child, out StringBuilder childCode, true);
		code.Append(childCode + "\n\n");
		foreach (var childRef in child.Children)
		{
			var childChild = childRef.TryGetConcept() as Class;
			if (childChild is null)
				continue;

			exportChildClass(childChild, child, code);
		}
	}

	public static void ExportClass(IClass class_, out StringBuilder code, bool isChild = false)
	{
		code = new StringBuilder("");

		exportClassHeader(class_, code);
		exportClassConstructor(class_, code);
		exportClassProperties(class_, code, isChild);
		exportClassToStringMethod(class_, code);
		exportClassMethods(class_, code);
		code.Append("\tpass\n");
	}

	private static void exportClassConstructor(IClass class_, StringBuilder code)
	{
		code.Append($"""
	def __init__(self, id):
		self._id = id
""");
		code.Append("\n");
		// export all array properties in constructor
		var allClassProperties = new HashSet<Property>();
		foreach (var property in class_.Properties)
		{
			if (property.ArrayBrackets.Count != 0)
				exportArrayPropertySetter(property, code);
		}
		
		Class? parent = class_.Parent?.TryGetConcept() as Class;
		if (class_.Parent is not null && parent is null)
			throw new ConceptNotFoundException("Parent of class " + class_.Id + " cannot be found.");

		// export all array properties from the parent classes
		while (parent is not null)
		{
			foreach (var property in parent.Properties)
			{
				if (!allClassProperties.Contains(property))
				{
					if (property.ArrayBrackets.Count != 0)
						exportArrayPropertySetter(property, code);
					allClassProperties.Add(property);
				}
			}

			var parentOfParent = parent.Parent?.TryGetConcept() as Class;
			if (parent.Parent is not null && parentOfParent is null)
				throw new ConceptNotFoundException("An ancestor of class " + class_.Id + " cannot be found.");
			parent = parentOfParent;
		}
		
		code.Append("\t\t\n");
	}

	private static void exportClassToStringMethod(IClass class_, StringBuilder code)
	{
		code.Append($"""
	def __str__(self):
		return self._id
""");
		code.Append("\n\t\n");
	}

	private static void exportClassHeader(IClass class_, StringBuilder code)
	{
		code.Append("class class_" + class_.GetPostfixId());
		if (class_.Parent is not null)
		{
			if (class_.Parent.GetPrefixId == BasicType.ID_PREFIX)
				code.Append("(" + class_.Parent.GetPostfixId + "):\n");
			else
				code.Append("(class_" + class_.Parent.GetPostfixId + "):\n");
		}
		else
		{
			code.Append(":\n");
		}
	}

	private static void exportClassProperties(IClass class_, StringBuilder code, bool isChild)
	{
		exportAllPropertiesNames(class_, code, isChild);
		HashSet<Property> allClassProperties = new HashSet<Property>();
		foreach (var property in class_.Properties)
		{
			if (property.ArrayBrackets.Count == 0)
				exportPropertySetter(property, code);
			// array properties are exported in the const
			allClassProperties.Add(property);
		}
		Class? parent = class_.Parent?.TryGetConcept() as Class;
		if (class_.Parent is not null && parent is null)
			throw new ConceptNotFoundException("Parent of class " + class_.Id + " cannot be found.");

		while (parent is not null)
		{
			foreach (var property in parent.Properties)
			{
				if (!allClassProperties.Contains(property))
				{
					if (property.ArrayBrackets.Count == 0)
						exportPropertySetter(property, code);
					// else exportArrayPropertySetter(property, code); // in constructor

					if (allClassProperties.Any(p => p.Name == property.Name))
						throw new TooManyPropertiesWithSameNameException($"During exporting {class_.Id}, Property " + property.Name + " has second definition, found in an ancestor class " + parent.Id + ".");

					allClassProperties.Add(property);
				}
			}

			code.Append("\tpass\n");

			var parentOfParent = parent.Parent?.TryGetConcept() as Class;
			if (parent.Parent is not null && parentOfParent is null)
				throw new ConceptNotFoundException("An ancestor of class " + class_.Id + " cannot be found.");
			parent = parentOfParent;
		}
	}

	private static void exportArrayPropertySetter(Property property, StringBuilder code)
	{
		code.Append($"\t\tself.{property.Name} = ArrayObject([");
		bool first = true;
		foreach (var arraySize in property.ArrayBrackets)
		{
			if (first)
			{
				code.Append(arraySize);
				first = false;
			}
			else
				code.Append($",{arraySize}");
		}
		code.Append($"], ");
		code.Append($"\"{property.Name}\", \"{property.Type.Id}\")\n");
	}

	private static void exportAllPropertiesNames(IClass class_, StringBuilder code, bool isChild)
	{
		code.Append("\tproperties = {\n");
		if (isChild)
		{
			code.Append("\t\t**class_" + class_.Parent!.Name + ".properties,\n");
		}
		code.Append("\t\t\"__object_name__\": \"@basicType:String\",\n");
		foreach (var property in class_.Properties)
		{
			code.Append("\t\t\"" + property.Name + "\": \"" + property.Type + "\"");
			code.Append(property != class_.Properties.Last() ? ",\n" : "\n");
		}
		code.Append("\t}\n");
	}

	private static void exportPropertySetter(Property property, StringBuilder code)
	{
		code.Append($"""
	@property
	def {property.Name}(self):
		return self._{property.Name}
		pass
	@{property.Name}.setter
	def {property.Name}(self, value):
		self._{property.Name} = value
		set_property("{property.Name}", value, self.properties["{property.Name}"], self.__object_name__)
""");
		code.Append("\n\t\n");
	}

	private static void exportClassMethods(IClass class_, StringBuilder code)
	{
		foreach (var method in class_.Methods)
		{
			ExportClassMethod(class_, method, out StringBuilder methodCode);
			code.Append((method == class_.Methods.First() ? "" : "\t\n") + methodCode);
		}
	}

	public static void ExportClassMethod(IClass class_, IMethod method, out StringBuilder code)
	{
		code = new StringBuilder();
		transformClassMethodsBody(class_, method, out StringBuilder bodyCode);
		code.Append(bodyCode);
	}

	private static void transformClassMethodsBody(IClass class_, IMethod method, out StringBuilder code)
	{
		code = new StringBuilder();
		if (method.Body is not null)
		{
			using (StringReader reader = new StringReader(method.Body.Code))
			{
				bool firstLine = true;
				string? line;
				while ((line = reader.ReadLine()) != null)
				{
					if (firstLine)
					{
						// methods header

						var header = new StringBuilder(line);

						var transformedlineOfCode = new StringBuilder(line);
						transformedlineOfCode.Replace("def ", "_");
						transformedlineOfCode.Replace(":", "");
						string underscoreMethodHeader = transformedlineOfCode.ToString();
						code.Append($"""
	{line}
		logging.info("run method ---->   {class_.Id}.{method.Id}")
		try:
			return self.{transformedlineOfCode.Replace("self,", "").Replace("self)", ")")}
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "{class_.Id}"
			method = "{method.Id}"
			raise MethodRuntimeException("An Exception in {class_.Id} during method {method.Id}\n{class_.Id}\n{method.Id}\n" + message)
""");
						code.Append("\n\t\n");
						code.Append($"""
	def {underscoreMethodHeader}:

""");
						firstLine = false;
					}
					else
					{
						transformLineOfCode(line, out StringBuilder transformedlineOfCode);
						code.Append("\t" + transformedlineOfCode + "\n");
					}
				}
				code.Append("\t\tpass\n");
			}
		}
	}

	private static void transformLineOfCode(string line, out StringBuilder lineOfCode)
	{
		lineOfCode = new StringBuilder(line);
		lineOfCode.Replace("@class:", "class_");
		lineOfCode.Replace("@object:", "objects.");
		lineOfCode.Replace("@event:", "events.");
	}


	public static void ExportGlobalMethod(String namePrefix, IMethod method, out StringBuilder code)
	{
		code = new StringBuilder();
		transformGlobalMethodsBody(namePrefix, method, out StringBuilder bodyCode);
		code.Append(bodyCode);
	}

	private static void transformGlobalMethodsBody(String namePrefix, IMethod method, out StringBuilder code)
	{
		code = new StringBuilder();
		if (method.Body is not null)
		{
			using (StringReader reader = new StringReader(method.Body.Code))
			{
				bool firstLine = true;
				string? line;
				while ((line = reader.ReadLine()) != null)
				{
					if (firstLine)
					{
						// methods header

						var header = new StringBuilder(line);

						var transformedlineOfCode = new StringBuilder(line);
						transformedlineOfCode.Replace("def ", "_");
						transformedlineOfCode.Replace(":", "");
						code.Append($"""
{namePrefix}{line}
	try:
		self.{transformedlineOfCode}
	except Exception as e:
		raise MethodRuntimeException("An Exception during method {method.Id}\n" + e.message)

def {namePrefix}{transformedlineOfCode}:

""");
						firstLine = false;
					}
					else
					{
						transformLineOfCode(line, out StringBuilder transformedlineOfCode);
						code.Append(transformedlineOfCode + "\n");
					}
				}
				code.Append("\tpass\n");
			}
		}
	}
}

[Serializable]
internal class TooManyPropertiesWithSameNameException : Exception
{
	public TooManyPropertiesWithSameNameException()
	{
	}

	public TooManyPropertiesWithSameNameException(string? message) : base(message)
	{
	}

	public TooManyPropertiesWithSameNameException(string? message, Exception? innerException) : base(message, innerException)
	{
	}

	protected TooManyPropertiesWithSameNameException(SerializationInfo info, StreamingContext context) : base(info, context)
	{
	}
}