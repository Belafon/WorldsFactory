using System.Collections.ObjectModel;
using System;
using System.Collections.Generic;
using WorldsFactory.world.library.classStructure.types;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.ids;

namespace WorldsFactory.world.objects;
public interface IObjectLoader
{
	public Task<ObservableCollection<WFObject>> Load();
	public void Save(WFObject obj);
	public void Rename(string oldId, WFObject obj, IMethod initMethod);
	public Task DeleteObject(WFObject obj);
	public WFObject CreateNewObject(string name, WFType type);
	public IMethod CreateInitMethod(WFObject wFObject);
	public IIDConceptManager IdManager { get; }
//	public Task<IMethod> LoadInitMethod(String objectName, WFType objectType);
}