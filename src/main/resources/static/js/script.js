function updateRowSum(input) {
    const qty = parseInt(input.value) || 0;
    const row = input.closest('tr');
    const price = parseFloat(row.querySelector('td:nth-child(3) span').textContent) || 0;
    row.querySelector('.row-sum').textContent = (price * qty).toFixed(2);
}