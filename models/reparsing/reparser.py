import sys

def format_num(x):
    res=0
    f = "{:.6f}".format(x)
    res= f.rstrip('0').rstrip('.') if '.' in f else f
    if res=="-0" or res=="-0.0":
        res="0"
    return res

def combine(input_paths, out_path, mtl_name, group_names):
    res=[]
    shiftv=0
    shiftvt=0
    shiftvn=0
    if len(group_names)!=len(input_paths):
        raise Exception("gime groups")
    for i in range(len(input_paths)):
        a,b,c,d=fix_obj(input_paths[i],mtl_name,group_names[i],i==0,shiftv,shiftvt,shiftvn)
        res.append(a)
        shiftv+=b
        shiftvt+=c
        shiftvn+=d
    with open("output/"+out_path,"w") as file:
        file.write("\n".join(res))
    print("finished and loaded to file: "+out_path)
    

def shift_f(st,sm):
    m=st.split("/")
    rm=[]
    if len(m)!=2 and len(m)!=3:
        raise Exception("incorrrect f len: "+str(len(m)))
    for i in range(len(m)):
        p=int(m[i])+int(sm[i])
        rm.append(str(p))
    return "/".join(rm)

def fix_obj(input_path,mtl_name, group,first_group,shiftv,shiftvt,shiftvn):
    
    vertices, uvs, normals, faces = [], [], [], []
    
    mtllib=None
    
    lastmtl=None
    
    s=True
    
    mnumv=0
    mnumvt=0
    mnumvn=0
    shft=[shiftv,shiftvt,shiftvn]
    
    with open("input/"+input_path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith('#') or line.startswith('o ') or line.startswith('g '):
                continue
            
            parts = line.split()
            prefix = parts[0]

            if prefix == 'v':
                vertices.append(f"v {' '.join(format_num(float(x)) for x in parts[1:])}")
                mnumv+=1
            elif prefix == 'vt':
                uvs.append(f"vt {' '.join(format_num(float(x)) for x in parts[1:])}")
                mnumvt+=1
            elif prefix == 'vn':
                normals.append(f"vn {' '.join(format_num(float(x)) for x in parts[1:])}")
                mnumvn+=1
            elif prefix == 'f':
                idx = parts[1:]
                if len(idx) > 3:
                    for i in range(1, len(idx) - 1):
                        faces.append(f"f {shift_f(idx[0],shft)} {shift_f(idx[i],shft)} {shift_f(idx[i+1],shft)}")
                else:
                    faces.append(f"f {shift_f(idx[0],shft)} {shift_f(idx[1],shft)} {shift_f(idx[2],shft)}")
            elif prefix == 'usemtl':
                if lastmtl==None or lastmtl != parts[1]:
                    faces.append("usemtl "+parts[1])
                    lastmtl=parts[1]
            elif prefix == "mtllib":
                mtllib=mtl_name
            elif prefix == "s":
                pass # already handling ts later
            else:
                raise Exception("unknown field: "+str(prefix))
            
    res=""
    if first_group:
        res+="# Fixed for ELN - Merged Objects\n"
        res+="mtllib "+mtllib+"\n"
    res+="o "+group+"\n"
    res+="\n".join(vertices) + "\n"
    res+="\n".join(uvs) + "\n"
    res+="\n".join(normals) + "\n"
    if s:
        res+="s off\n"
    res+="\n".join(faces) + "\n"  

    return res,mnumv,mnumvt,mnumvn

if __name__ == "__main__":
    #combine(["GridBreaker.obj","GridBreaker_nofuse.obj","GridBreaker_broken.obj"],
            #"GridBreaker.obj",
            #"GridBreaker.mtl",
            #["nofuse","broken"])
    #combine(["GridBreaker_hasfuse.obj"],
            #"GridBreaker_hasfuse.obj",
            #"GridBreaker.mtl",
            #["main"])
    #combine(["fuse_rods_spent.obj","fuse_rods_set.obj","GridBreaker_nofuse.obj"],
            #"GridBreaker.obj",
            #"GridBreaker.mtl",
            #["fuse_open","fuse_closed","main"])
    #combine(["fuse_rods_set.obj"],
            #"GridBreaker_isfuse.obj",
            #"GridBreaker.mtl",
            #["main"])
    #combine(["fuse_hand.obj"],
                #"GridFuseHand.obj",
                #"GridFuseHand.mtl",
                #["main"])
    combine(["stirling_engine_static.obj","stirling_engine_rotating.obj"],
                "StirlingEngine.obj",
                "StirlingEngine.mtl",
                ["static","rotating"])
    
    """
    шаблон для заполнения:
    
    combine(["название файла 1.obj","название файла 2.obj",...,"название файла n.obj"],
                "название файла для вывода.obj",
                "название файла материалов.mtl",
                ["название группы 1","название группы 2",...,"название группы n"])
    
    количество групп должно совпадать с количеством файлов
    каждый файл записывается в свою группу, для моделей из нескольких групп надо экспортировать модель
    несколько раз при этом каждый раз экспортируя части модели которую хочешь в группе
    
    .mtl файл указанный в опции 3 не обязан присутствовать гду либо, главное чтобы
    его название в данной строке совпадало с названием мтл файла который ты прикрепишь к модельке
    а он уже найдется на месте
    
    все .obj файлы указанные для ввода должны лежать в папке input
    выходной файл .obj со всеми группами будет в output
    его и копируешь в папку с моделькой прикладываю текстуры и .mtl с именем в опции 3
    
    """