using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WorldsFactory.project;


/// <summary>
/// Informations about the world, 
/// this is the content of the world file.
/// </summary>
public record WorldFileData(string Name, DateTime TimeOfCreation) { }