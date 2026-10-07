path = r'c:\Users\Lev\Downloads\Heart.fbx'
data = open(path, 'rb').read()
print('size', len(data), 'magic', data[:23])
for name in [b'Vertices', b'PolygonVertexIndex', b'Normals', b'UV', b'Mesh']:
    print(name, 'count', data.count(name), 'first', data.find(name))
