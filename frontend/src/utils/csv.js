// Shared CSV helpers for the Export / Import buttons.
//
// Excel on a Vietnamese Windows only reads UTF-8 CSV correctly when the file
// starts with a BOM, so buildCsv() always emits one and parseCsv() always
// strips one.

const BOM = '﻿'

function escapeCell(value) {
  const s = value === null || value === undefined ? '' : String(value)
  return `"${s.replace(/"/g, '""')}"`
}

// Builds CSV text from `rows` given `columns`: [{ key, label, get? }].
// `get(row)` overrides plain `row[key]` when a column needs formatting.
export function buildCsv(rows, columns) {
  const header = columns.map((c) => escapeCell(c.label ?? c.key)).join(',')
  const body = (rows || []).map((row) =>
    columns.map((c) => escapeCell(c.get ? c.get(row) : row?.[c.key])).join(','),
  )
  return BOM + [header, ...body].join('\r\n')
}

// Splits ONE csv line, honouring quoted cells and "" escapes. Returns cells
// with surrounding quotes removed.
export function parseCsvLine(line) {
  const cells = []
  let cur = ''
  let inQuotes = false

  for (let i = 0; i < line.length; i++) {
    const ch = line[i]
    if (inQuotes) {
      if (ch === '"') {
        if (line[i + 1] === '"') { cur += '"'; i++ } else { inQuotes = false }
      } else {
        cur += ch
      }
    } else if (ch === '"') {
      inQuotes = true
    } else if (ch === ',') {
      cells.push(cur); cur = ''
    } else {
      cur += ch
    }
  }
  cells.push(cur)
  return cells
}

// Parses whole CSV text into an array of objects keyed by the header row.
// Blank lines are skipped. Returns [] when there is no header.
export function parseCsv(text) {
  const clean = String(text ?? '').replace(/^﻿/, '')
  const lines = clean.split(/\r?\n/).filter((l) => l.trim() !== '')
  if (!lines.length) return []

  const header = parseCsvLine(lines[0]).map((h) => h.trim())
  return lines.slice(1).map((line) => {
    const cells = parseCsvLine(line)
    const obj = {}
    header.forEach((h, i) => { obj[h] = cells[i] ?? '' })
    return obj
  })
}

// Triggers a browser download of `text` as `filename`. Returns false instead of
// throwing when the environment has no DOM/Blob (tests, SSR).
export function downloadText(filename, text, mime = 'text/csv;charset=utf-8') {
  try {
    const blob = new Blob([text], { type: mime })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    return true
  } catch {
    return false
  }
}

// Same as downloadText but for a JSON payload (pretty-printed).
export function downloadJson(filename, payload) {
  try {
    return downloadText(filename, JSON.stringify(payload, null, 2), 'application/json')
  } catch {
    return false
  }
}

// Timestamp suffix for export filenames: 20260804-1005
export function stamp(now = new Date()) {
  const pad = (n) => String(n).padStart(2, '0')
  return `${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}-${pad(now.getHours())}${pad(now.getMinutes())}`
}
