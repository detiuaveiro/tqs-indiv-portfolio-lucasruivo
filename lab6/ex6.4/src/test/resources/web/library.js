function populateBooks(arr) {
  const tbody = document.getElementById('books');
  tbody.innerHTML = '';
  (arr || []).forEach(b => {
    const tr = document.createElement('tr');
    tr.setAttribute('data-title', b.title || '');
    tr.setAttribute('data-author', b.author || '');
    tr.setAttribute('data-category', b.category || '');
    tr.setAttribute('data-published', b.published || '');
    tr.innerHTML = `<td>${escapeHtml(b.title)}</td><td>${escapeHtml(b.author)}</td><td>${escapeHtml(b.category)}</td><td>${escapeHtml(b.published)}</td>`;
    tbody.appendChild(tr);
  });
  // run a filter pass to update visibility
  filterBooks();
}

function clearFilters() {
  document.getElementById('authorInput').value = '';
  document.getElementById('categoryInput').value = '';
  document.getElementById('titleInput').value = '';
  document.getElementById('fromDate').value = '';
  document.getElementById('toDate').value = '';
  filterBooks();
}

function filterBooks() {
  const author = document.getElementById('authorInput').value.trim();
  const category = document.getElementById('categoryInput').value.trim();
  const title = document.getElementById('titleInput').value.trim();
  const from = document.getElementById('fromDate').value;
  const to = document.getElementById('toDate').value;

  const rows = Array.from(document.querySelectorAll('#books tr'));
  let visibleCount = 0;
  rows.forEach(r => {
    const rTitle = r.getAttribute('data-title') || '';
    const rAuthor = r.getAttribute('data-author') || '';
    const rCategory = r.getAttribute('data-category') || '';
    const rPublished = r.getAttribute('data-published') || '';

    let visible = true;
    if (author && rAuthor !== author) visible = false;
    if (category && rCategory !== category) visible = false;
    if (title && rTitle !== title && !rTitle.includes(title)) visible = false;
    if (from) {
      if (!rPublished || rPublished < from) visible = false;
    }
    if (to) {
      if (!rPublished || rPublished > to) visible = false;
    }

    r.style.display = visible ? '' : 'none';
    if (visible) visibleCount++;
  });

  const noResults = document.getElementById('no-results');
  noResults.style.display = visibleCount === 0 ? '' : 'none';
}

function escapeHtml(text) {
  if (!text) return '';
  return text.replace(/[&<>\\\"]/g, function (c) { return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]; });
}
