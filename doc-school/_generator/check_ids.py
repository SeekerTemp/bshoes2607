# -*- coding: utf-8 -*-
"""Kiem bat bien ID: RQ->PB->Task lien tuc, truy vet du, dung thu tu sprint."""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from bshoes_data import RQS, PBS, TASKS, SPRINTS


def fail(m):
    print("FAIL:", m)
    sys.exit(1)


rq_ids = {r["id"] for r in RQS}
pb_ids = [p["id"] for p in PBS]
sprints = {s[0] for s in SPRINTS}

# 1. PB lien tuc PB-01..PB-nn, moi PB co RQ ton tai + sprint hop le
for i, p in enumerate(PBS, 1):
    if p["id"] != "PB-%02d" % i:
        fail("PB khong lien tuc tai %s (mong PB-%02d)" % (p["id"], i))
    if p["rq"] not in rq_ids:
        fail("%s tro RQ khong ton tai: %s" % (p["id"], p["rq"]))
    if p["sprint"] not in sprints:
        fail("%s sprint la %s" % (p["id"], p["sprint"]))

# 2. PB sap dung thu tu sprint (khong giam)
sp_seq = [p["sprint"] for p in PBS]
if sp_seq != sorted(sp_seq):
    fail("PB khong chay theo thu tu sprint: %s" % sp_seq)

# 3. Task lien tuc T-01.., Story ID tro PB ton tai
for i, t in enumerate(TASKS, 1):
    if t["id"] != "T-%02d" % i:
        fail("Task khong lien tuc tai %s (mong T-%02d)" % (t["id"], i))
    if t["story"] not in pb_ids:
        fail("%s Story ID khong ton tai: %s" % (t["id"], t["story"]))

# 4. Task nhom lien tuc theo PB (khong dan xen)
seen = []
for t in TASKS:
    if not seen or seen[-1] != t["story"]:
        if t["story"] in seen:
            fail("Task cua %s bi tach lam nhieu cho" % t["story"])
        seen.append(t["story"])

# 5. Backlog ID cua task = RQ cua story
pb_rq = {p["id"]: p["rq"] for p in PBS}
for t in TASKS:
    if t["backlog"] != pb_rq[t["story"]]:
        fail("%s Backlog ID %s != RQ cua story (%s)" % (t["id"], t["backlog"], pb_rq[t["story"]]))

print("OK: %d RQ, %d PB, %d Task. Bat bien ID dat." % (len(RQS), len(PBS), len(TASKS)))
