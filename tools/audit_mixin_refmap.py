import json
import os
import re
import zipfile

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))

def load_refmap():
	candidates = [
		os.path.join(ROOT, "build", "generated", "mixinRefmap", "eventjar.refmap.json"),
		os.path.join(ROOT, "build", "tmp", "compileJava", "eventjar.refmap.json"),
		os.path.join(ROOT, "build", "tmp", "compileJava", "compileJava-refmap.json"),
		os.path.join(ROOT, "dist", "eventjar-1.0-obf.jar"),
	]
	for path in candidates:
		if not os.path.exists(path):
			continue
		if path.endswith(".jar"):
			with zipfile.ZipFile(path) as zf:
				data = json.loads(zf.read("eventjar.refmap.json"))
			print("using", path)
			return data
		print("using", path)
		with open(path, encoding="utf-8") as fh:
			return json.load(fh)
	raise SystemExit("no refmap found")

def main():
	rm = load_refmap()
	maps = rm.get("mappings", {})
	issues = []

	mixin_root = os.path.join(ROOT, "src", "main", "java")
	for dirpath, _, files in os.walk(mixin_root):
		if "mixin" not in dirpath.replace("\\", "/"):
			continue
		for fn in files:
			if not fn.endswith(".java"):
				continue
			path = os.path.join(dirpath, fn)
			text = open(path, encoding="utf-8").read()
			pkg = re.search(r"package ([^;]+);", text).group(1)
			cls = pkg.replace(".", "/") + "/" + fn[:-5]
			entry = maps.get(cls, {})

			if "@Shadow" in text:
				issues.append(("SHADOW_REMAINING", cls, path))

			for field in re.findall(r'@Accessor\("([^"]+)"\)', text):
				if field not in entry:
					issues.append(("ACCESSOR_MISSING_REFMAP", cls, field, sorted(entry)))
				else:
					print("OK accessor", cls, field, "->", entry[field])

			for inv in re.findall(r'@Invoker(?:\("([^"]+)"\))?', text):
				name = inv if inv else "?"

				ok = name in entry or any(name == k or k.startswith(name + "(") for k in entry)
				if not ok:
					issues.append(("INVOKER_MISSING_REFMAP", cls, name, sorted(entry)))
				else:
					hit = [(k, entry[k]) for k in entry if k == name or k.startswith(name + "(")]
					print("OK invoker", cls, name, "->", hit)

			if "interface " in text:
				continue

			methods = set(re.findall(r'method\s*=\s*"([^"]+)"', text))
			for meth in methods:
				simple = meth.split("(")[0]
				ok = (
					simple in entry
					or meth in entry
					or any(k == simple or k.startswith(simple + "(") or simple in k for k in entry)
				)
				if not ok:
					issues.append(("INJECT_METHOD_MISSING_REFMAP", cls, meth, sorted(entry)))
				else:
					hit = [(k, entry[k]) for k in entry if k == simple or k.startswith(simple + "(") or simple in k]
					print("OK inject", cls, meth, "->", hit[:3])

	for cfg_name in ("mixins.eventjar.json", "outofbound.mixins.json"):
		cfg_path = os.path.join(ROOT, "src", "main", "resources", cfg_name)
		cfg = json.load(open(cfg_path, encoding="utf-8"))
		listed = set(cfg.get("mixins", []) + cfg.get("client", []) + cfg.get("server", []))
		pkg = cfg["package"].replace(".", "/")
		for dirpath, _, files in os.walk(os.path.join(mixin_root, *cfg["package"].split("."))):
			for fn in files:
				if not fn.endswith(".java"):
					continue
				simple = fn[:-5]
				text = open(os.path.join(dirpath, fn), encoding="utf-8").read()
				if "@Mixin" not in text:
					continue
				if simple not in listed:
					issues.append(("NOT_IN_MIXIN_CONFIG", cfg_name, simple))

	print("---ISSUES---")
	for item in issues:
		print(item)
	print("total issues", len(issues))
	return 1 if issues else 0

if __name__ == "__main__":
	raise SystemExit(main())
