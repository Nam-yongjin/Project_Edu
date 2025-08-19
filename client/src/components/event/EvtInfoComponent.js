import React from "react";

const SoftLabPrograms = () => {
  const programs = [
    { title: "예비교원 특화 프로그램", type: "교사교육", desc: "예비교원을 대상으로 에듀테크 활용 역량을 강화하는 특화 교육 프로그램입니다." },
    { title: "에듀테크 실증사업 교사 모집", type: "교사교육", desc: "현장 교사를 대상으로 에듀테크 실증에 참여할 기회를 제공하고 있습니다." },
  ];

  // 상단(연한 파랑/보라)과 어울리는 인디고 톤
  const theme = {
    underline: "border-indigo-500",
    cardBg: "bg-[#EEF2FF]",          // indigo-50
    cardBorder: "border-indigo-200", // indigo-200
    title: "text-indigo-700",        // 제목 강조
    type: "text-indigo-500/90",      // 유형 보조
  };

  return (
    <section className="bg-white py-16 px-8 font-sans text-gray-900">
      <div className="max-w-5xl mx-auto">
        <h1 className={`text-3xl font-semibold text-gray-900 mb-10 text-center tracking-wide border-b-2 ${theme.underline} pb-3`}>
          에듀테크 소프트랩 프로그램 안내
        </h1>

        <p className="text-lg text-center text-gray-700 mb-14 max-w-3xl mx-auto leading-relaxed">
          에듀테크 소프트랩은 교원·예비교사뿐 아니라 일반 시민도 참여할 수 있는
          체험·교육·공개행사를 운영합니다. 최신 에듀테크를 직접 경험해 보세요.
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {programs.map(({ title, type, desc }, i) => (
            <div
              key={i}
              className={`${theme.cardBg} rounded-xl p-6 shadow-sm border ${theme.cardBorder} hover:shadow-md transition-shadow duration-300`}
            >
              <h3 className={`text-xl font-semibold mb-2 ${theme.title}`}>{title}</h3>
              <p className={`text-sm italic ${theme.type} mb-4`}>{type}</p>
              <p className="text-gray-700 whitespace-pre-line">{desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default SoftLabPrograms;
