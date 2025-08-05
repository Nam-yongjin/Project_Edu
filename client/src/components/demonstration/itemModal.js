const ItemModal = ({ value, onChange, onConfirm, onClose }) => {
    const maxQty = 30;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-xl shadow-lg w-[320px] space-y-4">
                <h2 className="text-xl font-semibold text-center">수량 입력</h2>
                <input
                    type="number"
                    min={1}
                    max={maxQty}
                    value={value}
                    onChange={(e) => onChange(Number(e.target.value))}
                    className="w-full border p-2 rounded text-center text-lg"
                />
                <div className="flex justify-end gap-3 pt-2">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 rounded border hover:bg-gray-100"
                    >
                        취소
                    </button>
                    <button
                        onClick={() => {
                            if (value < 1) {
                                alert("수량은 1개 이상이어야 합니다.");
                                return;
                            }
                            if (value > maxQty) {
                                alert(`최대 수량은 ${maxQty}개입니다.`);
                                return;
                            }
                            onConfirm();
                        }}
                        className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
                    >
                        확인
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ItemModal;
