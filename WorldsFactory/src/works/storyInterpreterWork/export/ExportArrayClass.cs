using System;
using System.Text;

namespace WorldsFactory.works.storyInterpreterWork.export;

public class ExportArrayClass
{
	public void Export(out StringBuilder code)
	{
		code = new StringBuilder();
		code.Append("""
from collections import UserList

class CustomList(UserList):
	def __init__(self, *args, index, name, type):
		super().__init__(*args)
		self.index = index if index is not None else ()
		self.name = name
		self.type = type if type is not None else ""
		length = len(args)
		self.dimensions = "[" + str(length) + "]" if length > 0 else "[0]"
""");
		code.Append("\n\t\n");

		code.Append("""
	direct_access = False
	def __setitem__(self, index, value):
		indexes = self.index
		indexes += (index,)
		super().__setitem__(index, value)
		if not self.direct_access:
			set_array_property(self.name, value, self.type, indexes, self.__object_name__)
		else:
			self.direct_access = False
""");
		code.Append("\n\t\n");
		
		code.Append("""
	def set_objects_name(self, name):
		self.__object_name__ = name
		pass
""");
		code.Append("\n\n");

		code.Append("""
class ArrayObject:
	def __init__(self, shape, name, type):
		self._my_array = self._initialize_array(shape, (), name, type)
		self.dimensions = shape
""");
		code.Append("\n\t\n");

		code.Append("""
	def _initialize_array(self, shape, index, name, type):
		if len(shape) == 1:
			self.length = shape[0]
			return CustomList([None] * shape[0], index=index, name=name, type=type)
		else:
			return CustomList([self._initialize_array(shape[1:], index + (i,), name, type) for i in range(shape[0])], index=index, name=name, type=type)
""");
		code.Append("\n\t\n");

		code.Append("""
	def __getitem__(self, index):
		return self._get_value(self._my_array, index)
""");
		code.Append("\n\t\n");

		code.Append("""
	def __setitem__(self, index, value):
		self._set_value(self._my_array, index, value)
""");
		code.Append("\n\t\n");

		code.Append("""
	def _get_value(self, array, index):
		if isinstance(index, int):
			return array[index]
		else:
			return self._get_value(array[index[0]], index[1:])
""");
		code.Append("\n\t\n");

		code.Append("""
	def _set_value(self, array, index, value):
		if isinstance(index, int):
			array[index] = value
		else:
			self._set_value(array[index[0]], index[1:], value)
""");
		code.Append("\n\t\n");
		
		code.Append("""
	def set_objects_name(self, name):
		self.__object_name__ = name
		if isinstance(self._my_array, CustomList):
			self._my_array.set_objects_name(name)
		if self._my_array is not None:
			for i in self._my_array:
				if isinstance(i, CustomList):
					i.set_objects_name(name)
		pass
""");
		code.Append("\n\n");
	}

}
