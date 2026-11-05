import subprocess, urllib.request, json, time, websocket, re, sys

edge_path = r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
url = "https://drive.google.com/drive/folders/1hI8Ava7c3N8REXjiPA1yc6oilLS24zXh?usp=drive_link"
profile_dir = r"C:\Users\sanil\AppData\Local\Temp\edge_drive_sync"

proc = subprocess.Popen([
    edge_path, "--headless=new", "--remote-debugging-port=9222",
    "--disable-gpu", "--no-sandbox", f"--user-data-dir={profile_dir}",
    "--window-size=1920,1080", url
])

try:
    time.sleep(3)
    with urllib.request.urlopen("http://127.0.0.1:9222/json", timeout=5) as r:
        targets = json.loads(r.read())
        ws_url = next(t["webSocketDebuggerUrl"] for t in targets if t.get("type") == "page" and "drive.google.com" in t.get("url", ""))

    ws = websocket.create_connection(ws_url, timeout=20)
    msg_id = 1

    def send_cmd(method, params=None):
        global msg_id
        payload = {"id": msg_id, "method": method, "params": params or {}}
        ws.send(json.dumps(payload))
        msg_id += 1
        while True:
            resp = json.loads(ws.recv())
            if resp.get("id") == payload["id"]:
                return resp.get("result", {})

    def evaluate_js(script):
        res = send_cmd("Runtime.evaluate", {
            "expression": script,
            "returnByValue": True,
            "awaitPromise": True
        })
        return res.get("result", {}).get("value")

    time.sleep(5)

    print("Scrolling to load all Google Drive items...")
    for _ in range(40):
        evaluate_js("""
            (() => {
                let grid = document.querySelector('[role="main"]') || document.querySelector('[role="grid"]') || document.body;
                grid.focus();
                let ev = new KeyboardEvent('keydown', {key: 'PageDown', keyCode: 34, which: 34, bubbles: true});
                grid.dispatchEvent(ev);
                window.dispatchEvent(ev);
                document.querySelectorAll('*').forEach(el => {
                    if (el.scrollHeight > el.clientHeight) {
                        el.scrollTop += 2500;
                        el.dispatchEvent(new Event('scroll', {bubbles: true}));
                    }
                });
            })()
        """)
        time.sleep(1.2)

    items = evaluate_js("""
        (() => {
            let res = [];
            document.querySelectorAll('[aria-label]').forEach(el => {
                let lbl = el.getAttribute('aria-label') || '';
                if (lbl.includes('.jpg') || lbl.includes('.png') || lbl.includes('.jpeg') || lbl.includes('.webp')) {
                    let childWithId = el.querySelector('[data-id]') || el;
                    let fid = childWithId.getAttribute('data-id') || '';
                    if (!fid) {
                        let sskNode = el.querySelector('[ssk]') || el;
                        let ssk = sskNode.getAttribute('ssk') || '';
                        let m = ssk.match(/([a-zA-Z0-9_-]{25,40})/);
                        if (m) fid = m[1].replace(/-\\d+-\\d+$/, '');
                    }
                    res.push({label: lbl, id: fid});
                }
            });
            return res;
        })()
    """) or []

    wallpapers = {}
    for it in items:
        lbl = it["label"]
        fid = it["id"]
        m = re.search(r'([^\"]+\.(?:png|jpg|jpeg|webp))', lbl, re.IGNORECASE)
        if not m or not fid:
            continue
        fname = m.group(1).strip()
        if fid not in wallpapers:
            title = re.sub(r'^(IMG_\d+_\d+_|\d+_\d+_)', '', fname)
            title = re.sub(r'\.(png|jpg|jpeg|webp)$', '', title, flags=re.IGNORECASE)
            wallpapers[fid] = (title, fname)

    print(f"Total wallpapers extracted: {len(wallpapers)}")

    with open(r"E:\Android\google_drive_links.txt", "w", encoding="utf-8") as f:
        f.write(f"# Google Drive Wallpapers ({len(wallpapers)} wallpapers from folder: 1hI8Ava7c3N8REXjiPA1yc6oilLS24zXh)\n")
        f.write("# Format: Title | Google Drive URL\n\n")
        for fid, (t, fn) in wallpapers.items():
            f.write(f"{t} | https://drive.google.com/file/d/{fid}/view\n")

    print("Updated google_drive_links.txt successfully!")

finally:
    proc.terminate()
