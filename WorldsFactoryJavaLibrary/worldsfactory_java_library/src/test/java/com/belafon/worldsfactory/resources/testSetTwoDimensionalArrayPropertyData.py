from collections import UserList

class CustomList(UserList):
	def __init__(self, *args, index, name, type):
		super().__init__(*args)
		self.index = index if index is not None else ()
		self.name = name
		self.type = type if type is not None else ""
	
	direct_access = False
	def __setitem__(self, index, value):
		indexes = self.index
		indexes += (index,)
		print(f"CustomList: Setting value at index: {indexes} to: {value}")
		super().__setitem__(index, value)
		if not self.direct_access:
			set_array_property(self.name, value, self.type, indexes)
		else:
		    direct_access = False

	

class ArrayObject:
	def __init__(self, shape, name, type):
		print(f"Initializing ArrayObject with shape: {shape}")
		self._my_array = self._initialize_array(shape, (), name, type)
		self.dimensions = shape
	
	def _initialize_array(self, shape, index, name, type):
		print(f"Initializing array with shape: {shape}")
		if len(shape) == 1:
			return CustomList([None] * shape[0], index=index, name=name, type=type)
		else:
			return CustomList([self._initialize_array(shape[1:], index + (i,), name, type) for i in range(shape[0])], index=index, name=name, type=type)
	
	def __getitem__(self, index):
		print(f"Getting value at index: {index}")
		return self._get_value(self._my_array, index)
	
	def __setitem__(self, index, value):
		print(f"Setting value at index: {index} to: {value}")
		self._set_value(self._my_array, index, value)
	
	def _get_value(self, array, index):
		print(f"Getting value from array: {array} at index: {index}")
		if isinstance(index, int):
			return array[index]
		else:
			return self._get_value(array[index[0]], index[1:])
	
	def _set_value(self, array, index, value):
		print(f"Setting value in array: {array} at index: {index} to: {value}")
		if isinstance(index, int):
			array[index] = value
		else:
			self._set_value(array[index[0]], index[1:], value)
	
# ------------------- LIBRARY CLASSES -------------------

class MethodRuntimeException(Exception):
	def __init__(self, message="An exception occurred during method execution"):
		self.message = message
		super().__init__(self.message)

class class_MyClass:
	properties = {
		"myArray": "@basicType:String"
	}
	myArray = ArrayObject((2,2), "myArray", "@basicType:String")
	pass



# ------------------- LIBRARY OBJECTS -------------------

class Objects:
	_instance = None
	
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(Objects, cls).__new__(cls)
		return cls._instance
		
	MyObject = class_MyClass()
	pass

objects = Objects()

# ------------------- EVENTS -------------------


class event_myEvent:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_myEvent, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
	
	sequence_next_events = [
	]
	
	def loadConditionMethod(self):
		script_path = './myEvent_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
	
	def loadActionMethod(self):
		script_path = './myEvent_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
	
	def Condition(self):
		try:
			return self._Condition()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:myEvent"
			method = "@method:eventsClass_myEvent_Condition"
			raise MethodRuntimeException("An Exception in @class:myEvent during method @method:eventsClass_myEvent_Condition\n@class:myEvent\n@method:eventsClass_myEvent_Condition\n" + message)
	
	def _Condition(self):
		return False
		pass
	
	def Action(self):
		try:
			return self._Action()
		except Exception as e:
			message = e.message if hasattr(e, "message") else str(e)
			class_ = "@class:myEvent"
			method = "@method:eventsClass_myEvent_Action"
			raise MethodRuntimeException("An Exception in @class:myEvent during method @method:eventsClass_myEvent_Action\n@class:myEvent\n@method:eventsClass_myEvent_Action\n" + message)
	
	def _Action(self):
		pass
		pass


	
class Events:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(Events, cls).__new__(cls)
		return cls._instance
	myEvent = event_myEvent()
	pass

events = Events()

class event_start:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_start, cls).__new__(cls)
		return cls._instance
	conditionMethodLoaded = True
	actionMethodLoaded = True
	sequence_next_events = [
				"myEvent",
	]

class EventTreeHead:
	last_event = event_start()
	def try_move(self):
		for next_event_name in self.last_event.sequence_next_events:
			next_event = events.__getattribute__(next_event_name)
			if next_event.conditionMethodLoaded:
				try:
					if next_event.Condition():
						self.last_event = next_event
						self.last_event.Action()
						if event_tree_condition == EventGraphCondition.MOVE_UNTIL_POSSIBLE:
							self.try_move()
				except MethodRuntimeException as e:
					print("_exception_")
					print(e.message)
					print("_exception_end_")

event_tree_head = EventTreeHead()

# ------------------- PROPERTY SETTER -------------------

class LibraryEntryNotSetException(Exception):
	def __init__(self, message="library is not set"):
		self.message = message
		super().__init__(self.message)

def set_property(propertyName, value, type):
	print('_set_property_')
	print(propertyName)
	print(type)
	print(str(value))
	print('_set_property_end_')

def set_array_property(propertyName, value, type, index):
	print('_set_array_property_')
	print(propertyName)
	print(type)
	print(str(value))
	print(str(index))
	print('_set_array_property_end_')

# ------------------- EVENTS TREE -------------------

from enum import Enum

class EventGraphCondition(Enum):
	MOVE_MAX_BY_ONE = 1
	MOVE_UNTIL_POSSIBLE = 2

event_tree_condition = EventGraphCondition.MOVE_MAX_BY_ONE