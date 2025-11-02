const loadBtn = document.getElementById("loadBtn");
const tableBody = document.querySelector("#bookingsTable tbody");

loadBtn.addEventListener("click", async () => {
  const municipality = document.getElementById("municipalityInput").value.trim();
  
  const url = municipality
    ? `/api/bookings?municipality=${encodeURIComponent(municipality)}`
    : `/api/bookings`;

  try {
    const response = await fetch(url);
    const bookings = await response.json();

    tableBody.innerHTML = ""; // limpa tabela antes de reconstruir

    bookings.forEach(b => {
      const status = (b.status || "").toUpperCase();
      const isCancelled = status === "CANCELADO";

      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${b.municipality}</td>
        <td>${b.description}</td>
        <td>${b.requestedDate}</td>
        <td>${b.timeSlot || b.timeslot || "—"}</td>
        <td>${status}</td>
        <td>
          ${!isCancelled ? `
            <button onclick="updateStatus('${b.token}', 'EM_PROG')">Em Progresso</button>
            <button onclick="updateStatus('${b.token}', 'CONCLUIDO')">Concluído</button>
          ` : '<em>--------------</em>'}
        </td>
      `;
      tableBody.appendChild(tr);
    });
  } catch (err) {
    alert("Erro ao carregar bookings: " + err.message);
  }
});

async function updateStatus(token, status) {
  await fetch(`/api/bookings/${token}?status=${status}`, { method: "PUT" });
  alert(`Estado atualizado para ${status}`);
  loadBtn.click(); // recarrega imediatamente a tabela
}