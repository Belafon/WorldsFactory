using Newtonsoft.Json;
using Serilog;


namespace WorldsFactory.boot
{
	// TODO obsolete code, avalonias storage provider should be used instead
	public class FilesHandlers
	{
		public static string? loadFile(string path)
		{
			string content = "";
			if (File.Exists(path))
				content = File.ReadAllText(path);
			else
			{
				Log.Warning("FilesHandlers: loadFile, file does not exist " + path);
				return null;
			}
			return content;
		}

		public static DirectoryInfo? createDirectory(string name, string path)
		{
			string fileName = name;
			foreach (char c in System.IO.Path.GetInvalidFileNameChars())
				fileName = fileName.Replace(c, '_');
			if (!Uri.IsWellFormedUriString(path, UriKind.Absolute))
			{
				Log.Error("FilesHandlers: createDirectory, path is not well formed " + path);
				return null;
			}
			path += $"/{fileName}";
			if (Directory.Exists(path))
				try
				{
					return System.IO.Directory.CreateDirectory(path);
				}
				catch (Exception e)
				{
					Log.Error("FilesHandlers: createDirectory cannot create folder project " + e);
				}
			return null;
		}

		public async static void storeJson<T>(T t, string path)
		{
			string json = JsonConvert.SerializeObject(t, Formatting.Indented,
				new JsonSerializerSettings
				{
					PreserveReferencesHandling = PreserveReferencesHandling.Objects,
					TypeNameHandling = TypeNameHandling.All
				});
			await File.WriteAllTextAsync(path, json);
		}
		public async static void storeJsonWithoutObjectNames<T>(T t, string path)
		{
			string json = JsonConvert.SerializeObject(t, Formatting.Indented,
				new JsonSerializerSettings
				{
					PreserveReferencesHandling = PreserveReferencesHandling.Objects,
				});
			await File.WriteAllTextAsync(path, json);
		}

		public static T? loadJson<T>(string path)
		{
			T? obj = default(T);
			string? json = FilesHandlers.loadFile(path);
			if (json is not null)
			{
				obj = JsonConvert.DeserializeObject<T>(json,
					new JsonSerializerSettings
					{
						PreserveReferencesHandling = PreserveReferencesHandling.Objects,
						TypeNameHandling = TypeNameHandling.All
					});
			}
			else
			{
				Log.Error("FilesHandlers, fatal error, cannot load neccessary file" + path);
			}
			return obj;
		}

		public static T? loadJsonWithoutObjectNames<T>(string path)
		{
			T? obj = default(T);
			string? json = FilesHandlers.loadFile(path);
			if (json is not null)
			{
				obj = JsonConvert.DeserializeObject<T>(json,
					new JsonSerializerSettings { PreserveReferencesHandling = PreserveReferencesHandling.Objects });
			}
			else
			{
				Log.Error("FilesHandlers, fatal error, cannot load neccessary file" + path);
			}
			return obj;
		}


		public static void CopyDirectory(string sourceDir, string destinationDir, bool recursive)
		{
			// Get information about the source directory
			var dir = new DirectoryInfo(sourceDir);

			// Check if the source directory exists
			if (!dir.Exists)
				throw new DirectoryNotFoundException($"Source directory not found: {dir.FullName}");
				
			var dirOut = new DirectoryInfo(destinationDir);

			// Check if the source directory exists
			if (!dirOut.Exists)
			{
				try
				{
					dirOut.Create();
				}
				catch (Exception e)
				{
					Log.Error("FilesHandlers: CopyDirectory, cannot create folder project " + e);
				}				
			}

			// Cache directories before we start copying
			DirectoryInfo[] dirs = dir.GetDirectories();

			// Get the files in the source directory and copy to the destination directory
			foreach (FileInfo file in dir.GetFiles())
			{
				string targetFilePath = Path.Combine(destinationDir, file.Name);
				file.CopyTo(targetFilePath);
			}

			// If recursive and copying subdirectories, recursively call this method
			if (recursive)
			{
				foreach (DirectoryInfo subDir in dirs)
				{
					string newDestinationDir = Path.Combine(destinationDir, subDir.Name);
					CopyDirectory(subDir.FullName, newDestinationDir, true);
				}
			}
		}
	}


}
