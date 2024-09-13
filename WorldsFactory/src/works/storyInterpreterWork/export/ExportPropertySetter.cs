using System.Text;

namespace WorldsFactory.works.storyInterpreterWork.export;

internal class ExportPropertySetter
{
	internal void Export(out StringBuilder code)
	{
		code = new StringBuilder();
		code.Append(
""" 
# ------------------- PROPERTY SETTER -------------------

class LibraryEntryNotSetException(Exception):
	def __init__(self, message="library is not set"):
		self.message = message
		super().__init__(self.message)

def set_property(propertyName, value, type, objectName):
	print('_set_property_')
	print(propertyName)
	print(type)
	print(objectName)
	print(str(value))
	print('_set_property_end_')

def set_array_property(propertyName, value, type, index, objectName):
	print('_set_array_property_')
	print(propertyName)
	print(type)
	print(objectName)
	print(str(value))
	print(str(index))
	print('_set_array_property_end_')

""");
		code.Append("\n");
	}
}